package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.RepositoryProductVO;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderDetail;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderMaster;
import com.jizhangyl.application.dataobject.primary.RepositoryDelivery;
import com.jizhangyl.application.dataobject.primary.RepositoryProduct;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.OrderDetailService;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.PurchaseOrderService;
import com.jizhangyl.application.service.RepositoryDeliveryService;
import com.jizhangyl.application.service.RepositoryProductService;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import com.jizhangyl.application.utils.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/23 15:32
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/repository/product")
public class RepositoryProductManageController {

    @Autowired
    private OrderMasterService orderMasterService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private RepositoryProductService repositoryProductService;

    @Autowired
    private RepositoryDeliveryService repositoryDeliveryService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/list")
    public ResultVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "20") Integer size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<RepositoryProduct> repositoryProductPage = repositoryProductService.findAll(pageRequest);

        List<OrderMaster> orderMasterList = orderMasterService.findByOrderStatus(OrderStatusEnum.PAID.getCode());

        List<String> orderIdList = orderMasterList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());

        List<OrderDetail> orderDetailList = orderMasterService.findDetailByOrderIdIn(orderIdList);

        // 查询昨日仓库发货记录
        List<RepositoryDelivery>  yestDeliveryList = repositoryDeliveryService.findByCreateTimeBetween(DateUtil.getStart(-1), DateUtil.getStart(0));

        List<String> yestDeliveryOrderId = yestDeliveryList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());

        List<OrderDetail> yestDetailList = orderMasterService.findDetailByOrderIdIn(yestDeliveryOrderId);

        List<RepositoryDelivery>  weekDeliveryList = repositoryDeliveryService.findByCreateTimeBetween(DateUtil.getStart(-7), DateUtil.getStart(0));

        List<String> weekDeliveryOrderId = weekDeliveryList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());

        List<OrderDetail> weekDetailList = orderMasterService.findDetailByOrderIdIn(weekDeliveryOrderId);

        List<RepositoryProductVO> repositoryProductVOList = repositoryProductPage.getContent().stream()
                .map(e -> {
                    RepositoryProductVO repositoryProductVO = new RepositoryProductVO();
                    BeanUtils.copyProperties(e, repositoryProductVO);

                    int todoOutNum = 0;

                    for (OrderDetail orderDetail : orderDetailList) {
                        if (orderDetail.getProductId().equals(String.valueOf(e.getProductId()))) {
                            todoOutNum += orderDetail.getProductQuantity();
                        }
                    }

                    repositoryProductVO.setTodoOutNum(todoOutNum);

                    int yestOutNum = 0;

                    for (OrderDetail orderDetail : yestDetailList) {
                        if (orderDetail.getProductId().equals(String.valueOf(e.getProductId()))) {
                            yestOutNum += orderDetail.getProductQuantity();
                        }
                    }

                    repositoryProductVO.setYestOutNum(yestOutNum);

                    int weekOutNum = 0;

                    for (OrderDetail orderDetail : weekDetailList) {
                        if (orderDetail.getProductId().equals(String.valueOf(e.getProductId()))) {
                            weekOutNum += orderDetail.getProductQuantity();
                        }
                    }

                    repositoryProductVO.setWeekAvgOutNum(weekOutNum / 7);

                    return repositoryProductVO;
                }).collect(Collectors.toList());

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("data", repositoryProductVOList);
        resultMap.put("totalPage", repositoryProductPage.getTotalPages());

        return ResultVOUtil.success(resultMap);
    }


    /**
     * 每个商品的进库记录（默认查询一周）
     * @param productId
     * @return
     */
    @GetMapping("/inDetail")
    public ResultVO inDetail(@RequestParam("productId")Integer productId,
                             @RequestParam(value = "beginTime", required = false) String beginTimeStr,
                             @RequestParam(value = "endTime", required = false) String endTimeStr,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (productId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        Map<String, Date> formatDate = dateFormat(beginTimeStr, endTimeStr);
        Date beginTime = formatDate.get("beginTime");
        Date endTime = formatDate.get("endTime");
        // 分页参数
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);

        /**
         * 采购订单入库（purchase_order_master）
         */
        List<PurchaseOrderMaster> purchaseOrderMasterList = purchaseOrderService.findByCreateTimeBetween(beginTime, endTime);

        List<String> purchaseOrderMasterIdList = purchaseOrderMasterList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());

        Page<PurchaseOrderDetail> purchaseOrderDetailPage = purchaseOrderService.findDetailByOrderIdInAndProductId(purchaseOrderMasterIdList,
                productId, pageRequest);

        /*List<Map<String, Object>> inList = new ArrayList<>();

        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetailList) {
            if (purchaseOrderDetail.getProductId().equals(productId)) {
                Map<String, Object> map = new HashMap<>();
                map.put("orderId", purchaseOrderDetail.getOrderId());
                map.put("productQuantity", purchaseOrderDetail.getProductQuantity());
                map.put("createTime", sdf.format(purchaseOrderDetail.getCreateTime()));
                inList.add(map);
            }
        }*/

        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("data", purchaseOrderDetailPage.getContent());
        resultMap.put("totalPage", purchaseOrderDetailPage.getTotalPages());

        return ResultVOUtil.success(resultMap);
    }

    /**
     * 每个商品的出库记录（默认查询一周）
     * @param productId
     * @return
     */
    @GetMapping("/outDetail")
    public ResultVO outDetail(@RequestParam("productId")Integer productId,
                              @RequestParam(value = "beginTime", required = false) String beginTimeStr,
                              @RequestParam(value = "endTime", required = false) String endTimeStr,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "size", defaultValue = "10") Integer size) {

        if (productId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        Map<String, Date> formatDate = dateFormat(beginTimeStr, endTimeStr);
        Date beginTime = formatDate.get("beginTime");
        Date endTime = formatDate.get("endTime");

        //分页参数
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);

        /**
         * 订单发货（repository_delivery）
         */
        List<RepositoryDelivery> repositoryDeliveryList = repositoryDeliveryService.findByCreateTimeBetween(beginTime, endTime);

        List<String> orderIdList = repositoryDeliveryList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());

        List<OrderMaster> orderMasterList = orderMasterService.findByOrderIdIn(orderIdList);

        Page<OrderDetail> orderDetailPage = orderMasterService.findDetailByOrderIdInAndProductId(orderIdList, String.valueOf(productId), pageRequest);

        List<Map<String, Object>> outList = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetailPage.getContent()) {
            if (orderDetail.getProductId().equals(String.valueOf(productId))) {

                Map<String, Object> map = new HashMap<>();

                for (OrderMaster orderMaster : orderMasterList) {
                    if (orderMaster.getOrderId().equals(orderDetail.getOrderId())) {
                        map.put("expressNum", orderMaster.getExpressNumber());
                        break;
                    }
                }

                map.put("productQuantity", orderDetail.getProductQuantity());
                map.put("createTime", sdf.format(orderDetail.getCreateTime()));
                outList.add(map);
            }
        }

        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("data", outList);
        resultMap.put("totalPage", orderDetailPage.getTotalPages());

        return ResultVOUtil.success(resultMap);
    }

    /**
     * 按照jan_code, 商品名称, 仓库打包识别码 查询仓库商品
     * @param param
     * @return
     */
    @GetMapping("/findByCriteria")
    public ResultVO findByCriteria(@RequestParam("param") String param,
                                   @RequestParam(value = "status", defaultValue = "0") Integer status,
                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (param == null) {
            param = "";
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<RepositoryProduct> repositoryProductPage = repositoryProductService.findByCriteria(param, status, pageRequest);

        Map<String, Object> resultMap = new HashMap<String, Object>() {
            {
                put("data", repositoryProductPage.getContent());
                put("totalPage", repositoryProductPage.getTotalPages());
            }
        };

        return ResultVOUtil.success(resultMap);
    }

    @GetMapping("/exportByCriteria")
    public ResponseEntity<byte[]> exportByCriteria(@RequestParam("param") String param,
                                     @RequestParam(value = "status", defaultValue = "0") Integer status) {
        if (param == null) {
            param = "";
        }

        List<RepositoryProduct> repositoryProductList = repositoryProductService.findByCriteria(param, status);

        if (CollectionUtils.isEmpty(repositoryProductList)) {
            throw new GlobalException(ResultEnum.PRODUCT_LIST_EMPTY);
        }

        HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            String dateStr = sdf.format(new Date());
            String fileName = "仓库商品列表" + dateStr + ".xls";

            headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("utf-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            baos = new ByteArrayOutputStream();

//            FileOutputStream out = new FileOutputStream("D:\\test.xls");

            ExcelUtil<RepositoryProduct> excelUtil = new ExcelUtil(RepositoryProduct.class);
            excelUtil.exportExcel(repositoryProductList, "仓库商品列表", 65536, baos);
            log.warn("【仓库商品信息导出】{} 导出成功", fileName);

        } catch (Exception e) {
            log.warn("【仓库商品信息导出】导出错误, {}", e);
            throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
        }

        return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.CREATED);
    }


    public Map<String, Date> dateFormat(String beginTimeStr, String endTimeStr) {

        Map<String, Date> dateMap = new HashMap<>(2);
        Date beginTime = null;
        Date endTime = null;

        boolean singleEmpty = (!StringUtils.isEmpty(beginTimeStr) && StringUtils.isEmpty(endTimeStr))
                || (StringUtils.isEmpty(beginTimeStr) && !StringUtils.isEmpty(endTimeStr));
        boolean doubleEmpty = StringUtils.isEmpty(beginTimeStr) && StringUtils.isEmpty(endTimeStr);

        if (singleEmpty) {
            log.error("【出入库记录】beginTime = {}, endTime = {}", beginTime, endTime);
            throw new GlobalException(ResultEnum.DATE_FORMAT_ERROR);
        } else if (doubleEmpty) {
            endTime = new Date();
            beginTime = DateUtil.getStart(-7);
        } else {
            try {
                beginTime = sdf.parse(beginTimeStr);
                endTime = sdf.parse(endTimeStr);
            } catch (ParseException e) {
                throw new GlobalException(ResultEnum.DATE_FORMAT_ERROR);
            }
        }

        dateMap.put("beginTime", beginTime);
        dateMap.put("endTime", endTime);

        return dateMap;
    }
}
