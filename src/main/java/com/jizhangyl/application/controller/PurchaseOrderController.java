package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ConfirmCertVO;
import com.jizhangyl.application.VO.PayCertVO;
import com.jizhangyl.application.VO.ProviderProductRelatVo;
import com.jizhangyl.application.VO.PurchaseConfirmDetailVO;
import com.jizhangyl.application.VO.PurchaseOrderDetailVO;
import com.jizhangyl.application.VO.PurchasePayDetailVO;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.converter.PurchaseOrderConfirmForm2PurchaseOrderDto;
import com.jizhangyl.application.converter.PurchaseOrderForm2PurchaseOrderDto;
import com.jizhangyl.application.converter.PurchaseOrderPayForm2PurchaseOrderDto;
import com.jizhangyl.application.dataobject.primary.ConfirmCert;
import com.jizhangyl.application.dataobject.primary.PayCert;
import com.jizhangyl.application.dataobject.primary.PurchaseConfirmDetail;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderConfirm;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderDetail;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderMaster;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderPay;
import com.jizhangyl.application.dataobject.primary.PurchasePayDetail;
import com.jizhangyl.application.dataobject.primary.Shop;
import com.jizhangyl.application.dataobject.primary.VerifyCert;
import com.jizhangyl.application.dto.PurchaseOrderDto;
import com.jizhangyl.application.enums.PurchaseOrderStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.PurchaseOrderConfirmForm;
import com.jizhangyl.application.form.PurchaseOrderForm;
import com.jizhangyl.application.form.PurchaseOrderPayForm;
import com.jizhangyl.application.service.ConfirmCertService;
import com.jizhangyl.application.service.PayCertService;
import com.jizhangyl.application.service.ProviderProductRelatService;
import com.jizhangyl.application.service.PurchaseOrderConfirmService;
import com.jizhangyl.application.service.PurchaseOrderPayService;
import com.jizhangyl.application.service.PurchaseOrderService;
import com.jizhangyl.application.service.RepositoryProductService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.service.VerifyCertService;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/20 14:35
 * @description 采购订单
 */
@Slf4j
@RestController
@RequestMapping("/purchase/order")
public class PurchaseOrderController {

    @Autowired
    private VerifyCertService verifyCertService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private ProviderProductRelatService providerProductRelatService;

    @Autowired
    private ConfirmCertService confirmCertService;

    @Autowired
    private PayCertService payCertService;

    @Autowired
    private PurchaseOrderConfirmService purchaseOrderConfirmService;

    @Autowired
    private PurchaseOrderPayService purchaseOrderPayService;

    @Autowired
    private RepositoryProductService repositoryProductService;

    @Autowired
    private ShopService shopService;

    /**
     * 新增采购单
     * @param purchaseOrderForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/create")
    public ResultVO create(@Valid PurchaseOrderForm purchaseOrderForm,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        PurchaseOrderDto purchaseOrderDto = PurchaseOrderForm2PurchaseOrderDto.convert(purchaseOrderForm);
        PurchaseOrderDto result = purchaseOrderService.create(purchaseOrderDto);
        return ResultVOUtil.success(result);
    }

    /**
     * 修改采购单
     * @param purchaseOrderForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/update")
    public ResultVO update(@Valid PurchaseOrderForm purchaseOrderForm,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors() || StringUtils.isEmpty(purchaseOrderForm.getOrderId())) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        PurchaseOrderDto purchaseOrderDto = PurchaseOrderForm2PurchaseOrderDto.convert(purchaseOrderForm);

        PurchaseOrderDto result = purchaseOrderService.update(purchaseOrderDto);

        return ResultVOUtil.success(result);
    }

    @GetMapping("/list")
    public ResultVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<PurchaseOrderMaster> purchaseOrderMasterPage = purchaseOrderService.findAll(pageRequest);

        Map<String, Object> map = new HashMap<>();
        map.put("data", purchaseOrderMasterPage.getContent());
        map.put("totalPage", purchaseOrderMasterPage.getTotalPages());

        return ResultVOUtil.success(map);
    }

    @GetMapping("/findByOrderIdOrProviderName")
    public ResultVO findByOrderIdOrProviderName(@RequestParam(value = "orderId", required = false) String orderId,
                                                @RequestParam(value = "providerName", required = false)String providerName,
                                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        if (!StringUtils.isEmpty(orderId)) {
            PurchaseOrderMaster purchaseOrderMaster = purchaseOrderService.findByOrderId(orderId);
            result.put("data", purchaseOrderMaster);
            result.put("totalPage", 1);
        } else if (!StringUtils.isEmpty(providerName)) {
            // 按照供应商名字模糊查询
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Page<PurchaseOrderMaster> purchaseOrderMasterPage = purchaseOrderService.findByroviderName(providerName, pageRequest);
            result.put("data", purchaseOrderMasterPage.getContent());
            result.put("totalPage", purchaseOrderMasterPage.getTotalPages());
        }
        return ResultVOUtil.success(result);
    }

    @GetMapping("/detail")
    public ResultVO detail(@RequestParam(value = "orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<PurchaseOrderDetail> purchaseOrderDetailList = purchaseOrderService.findDetailByOrderId(orderId);
        List<PurchaseOrderDetailVO> purchaseOrderDetailVOList = new ArrayList<>();
        List<Integer> productIdList = purchaseOrderDetailList.stream().map(e -> e.getProductId()).collect(Collectors.toList());
        List<Shop> shopList = shopService.findByIdIn(productIdList);
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetailList) {
            PurchaseOrderDetailVO purchaseOrderDetailVO = new PurchaseOrderDetailVO();
            BeanUtils.copyProperties(purchaseOrderDetail, purchaseOrderDetailVO);
            for (Shop shop : shopList) {
                if (shop.getId().equals(purchaseOrderDetail.getProductId())) {
                    purchaseOrderDetailVO.setBoxNum(shop.getShopXcount());
                    break;
                }
            }
            purchaseOrderDetailVOList.add(purchaseOrderDetailVO);
        }
        return ResultVOUtil.success(purchaseOrderDetailVOList);
    }

    @GetMapping("/cancel")
    public ResultVO cancel(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        purchaseOrderService.cancel(orderId);
        return ResultVOUtil.success();
    }

    /**
     * 一个采购订单对应多次支付情况
     * @param orderId
     * @param realAmount
     * @return
     */
    @GetMapping("/paid")
    public ResultVO paid(@RequestParam("orderId") String orderId, @RequestParam("realAmount") String realAmount) {
        if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(realAmount)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        purchaseOrderService.paid(orderId, realAmount);
        return ResultVOUtil.success();
    }

    /**
     * 按照商品名称或者 JANCODE 查询供应商能提供的商品列表
     * @return
     */
    @GetMapping("/findByNameOrJan")
    public ResultVO findProviderShopByProductNameOrJancode(@RequestParam(value = "param") String param,
                                                           @RequestParam(value = "providerId") Integer providerId) {
        if (StringUtils.isEmpty(param)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        // 参数可能为 productName 和 jancode
        List<ProviderProductRelatVo> providerProductRelatVoList = providerProductRelatService.findProviderShopByProductNameOrJancode(providerId, param);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ProviderProductRelatVo providerProductRelatVo : providerProductRelatVoList) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", providerProductRelatVo.getProductId());
            map.put("productName", providerProductRelatVo.getProductName());
            map.put("productJancode", providerProductRelatVo.getProductJancode());
            resultList.add(map);
        }
        return ResultVOUtil.success(resultList);
    }


    /**
     * 根据订单编号查询采购订单详情
     *
     * @return
     */
    @GetMapping("/findOrderById")
    public ResultVO findOrderById(@RequestParam("orderId") String orderId,
                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                  @RequestParam(value = "size", defaultValue = "5") Integer size) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<PurchaseOrderDetail> purchaseOrderDetailPage = purchaseOrderService.findOrderById(orderId, pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", purchaseOrderDetailPage.getContent());
        result.put("totalPage", purchaseOrderDetailPage.getTotalPages());

        return ResultVOUtil.success(result);
    }


    /**
     * 仓库确定采购订单（一个采购订单对应多次实收）
     * @param purchaseOrderConfirmForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/confirmOrder")
    public ResultVO confirmOrder(@Valid PurchaseOrderConfirmForm purchaseOrderConfirmForm,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        PurchaseOrderDto purchaseOrderDto = PurchaseOrderConfirmForm2PurchaseOrderDto.convert(purchaseOrderConfirmForm);
        PurchaseOrderDto result = purchaseOrderService.confirm(purchaseOrderDto);
        return ResultVOUtil.success(result);
    }

    @GetMapping("/getConfirmList")
    public ResultVO getConfirmList(@RequestParam("orderId") String orderId) {
        if (org.springframework.util.StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY_WITH_DETAIL.getCode(),
                    String.format(ResultEnum.PARAM_EMPTY_WITH_DETAIL.getMessage(), "orderId"));
        }
        List<PurchaseOrderConfirm> purchaseOrderConfirmList = purchaseOrderConfirmService.findByOrderId(orderId);

        List<String> confirmIdList = purchaseOrderConfirmList.stream().map(e -> e.getConfirmId()).collect(Collectors.toList());

        // 查询请求详情信息
        List<PurchaseConfirmDetail> purchaseConfirmDetailList = purchaseOrderService.findConfirmDetailByConfirmIdIn(confirmIdList);

        // 查询请求凭证信息
        List<ConfirmCert> confirmCertList = confirmCertService.findByConfirmIdIn(confirmIdList);

        List<PurchaseConfirmDetailVO> purchaseConfirmDetailVOList = new ArrayList<>();

        for (PurchaseOrderConfirm purchaseOrderConfirm : purchaseOrderConfirmList) {

            PurchaseConfirmDetailVO purchaseConfirmDetailVO = new PurchaseConfirmDetailVO();

            // 加入每次确认的凭证组
            List<ConfirmCertVO> ConfirmCertVOList = new ArrayList<>();

            for (ConfirmCert confirmCert : confirmCertList) {
                if (purchaseOrderConfirm.getConfirmId().equals(confirmCert.getConfirmId())
                        && !StringUtils.isEmpty(confirmCert.getConfirmCert())) {
                    ConfirmCertVO confirmCertVO = new ConfirmCertVO(confirmCert.getConfirmCert(), confirmCert.getCreateTime());
                    ConfirmCertVOList.add(confirmCertVO);
                }
            }

            purchaseConfirmDetailVO.setCertList(ConfirmCertVOList);

            // 加入确认的商品详情

            List<PurchaseConfirmDetail> detailList = new ArrayList<>();

            for (PurchaseConfirmDetail purchaseConfirmDetail : purchaseConfirmDetailList) {
                if (purchaseOrderConfirm.getConfirmId().equals(purchaseConfirmDetail.getConfirmId())) {
                    detailList.add(purchaseConfirmDetail);
                }
            }

            purchaseConfirmDetailVO.setPurchaseConfirmDetailList(detailList);

            purchaseConfirmDetailVOList.add(purchaseConfirmDetailVO);

        }

        return ResultVOUtil.success(purchaseConfirmDetailVOList);
    }

    /**
     * 获取某一采购订单当前已经实收的商品信息（第一次付款前一般已经全部完成实收）
     * @param orderId
     * @return
     */
    @GetMapping("/getCurrentRecv")
    public ResultVO getCurrentRecv(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        // 根据订单号码查出所有的实收数据
        List<PurchaseOrderConfirm> purchaseOrderConfirmList = purchaseOrderConfirmService.findByOrderId(orderId);

        List<String> confirmIdList = purchaseOrderConfirmList.stream().map(e -> e.getConfirmId()).collect(Collectors.toList());

        // 查询所有的确认订单详情
        List<PurchaseConfirmDetail> purchaseConfirmDetailList = purchaseOrderService.findConfirmDetailByConfirmIdIn(confirmIdList);

        List<PurchaseConfirmDetail> resultList = new ArrayList<>();

        for (PurchaseConfirmDetail purchaseConfirmDetail : purchaseConfirmDetailList) {

            boolean isExist = false;

            for (PurchaseConfirmDetail confirmDetail : resultList) {
                if (confirmDetail.getProductId().equals(purchaseConfirmDetail.getProductId())) {
                    isExist = true;
                    confirmDetail.setProductQuantity(confirmDetail.getProductQuantity() + purchaseConfirmDetail.getProductQuantity());
                    break;
                }
            }

            if (!isExist) {
                resultList.add(purchaseConfirmDetail);
            }
        }
        return ResultVOUtil.success(resultList);
    }

    /**
     * 付款
     */
    @PostMapping("/orderPay")
    public ResultVO orderPay(@Valid PurchaseOrderPayForm purchaseOrderPayForm,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        PurchaseOrderDto purchaseOrderDto = PurchaseOrderPayForm2PurchaseOrderDto.convert(purchaseOrderPayForm);
        PurchaseOrderDto result = purchaseOrderService.pay(purchaseOrderDto);
        return ResultVOUtil.success(result);
    }

    @GetMapping("/getLoanDate")
    public ResultVO getLoanDate(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderService.findByOrderId(orderId);

        return ResultVOUtil.success(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purchaseOrderMaster.getPayDate()));
    }

    /**
     * 获取历史付款记录
     */
    /*@GetMapping("/getCurrentPay")
    public ResultVO getCurrentPay(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderService.findByOrderId(orderId);

        // 根据订单号码查出所有的实收数据
        List<PurchaseOrderPay> purchaseOrderPayList = purchaseOrderPayService.findByOrderId(orderId);

        List<String> payIdList = purchaseOrderPayList.stream().map(e -> e.getPayId()).collect(Collectors.toList());

        // 查询所有的确认订单详情
        List<PurchasePayDetail> purchasePayDetailList = purchaseOrderService.findPayDetailByPayIdIn(payIdList);

        // 查询支付凭证信息
        List<PayCert> payCertList = payCertService.findByPayIdIn(payIdList);

        List<PurchasePayDetail> resultList = new ArrayList<>();

        for (PurchasePayDetail purchasePayDetail : purchasePayDetailList) {

            boolean isExist = false;

            for (PurchasePayDetail payDetail : resultList) {
                if (payDetail.getProductId().equals(purchasePayDetail.getProductId())) {
                    isExist = true;
                    payDetail.setProductQuantity(payDetail.getProductQuantity() + purchasePayDetail.getProductQuantity());
                    break;
                }
            }

            if (!isExist) {
                resultList.add(purchasePayDetail);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("payDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purchaseOrderMaster.getPayDate()));
        map.put("detail", resultList);

        return ResultVOUtil.success(map);
    }*/


    @GetMapping("/getPayList")
    public ResultVO getPayList(@RequestParam("orderId") String orderId) {
        if (org.springframework.util.StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY_WITH_DETAIL.getCode(),
                    String.format(ResultEnum.PARAM_EMPTY_WITH_DETAIL.getMessage(), "orderId"));
        }

        List<PurchaseOrderPay> purchaseOrderPayList = purchaseOrderPayService.findByOrderId(orderId);

        List<String> payIdList = purchaseOrderPayList.stream().map(e -> e.getPayId()).collect(Collectors.toList());

        // 查询请求详情信息
        List<PurchasePayDetail> purchasePayDetailList = purchaseOrderService.findPayDetailByPayIdIn(payIdList);

        // 查询请求凭证信息
        List<PayCert> payCertList = payCertService.findByPayIdIn(payIdList);

        List<PurchasePayDetailVO> purchaseConfirmDetailVOList = new ArrayList<>();

        for (PurchaseOrderPay purchaseOrderPay : purchaseOrderPayList) {

            PurchasePayDetailVO purchasePayDetailVO = new PurchasePayDetailVO();

            // 加入每次确认的凭证组
            List<PayCertVO> payCertVOList = new ArrayList<>();

            for (PayCert payCert : payCertList) {
                if (purchaseOrderPay.getPayId().equals(payCert.getPayId())
                        && !StringUtils.isEmpty(payCert.getPayCert())) {
                    PayCertVO payCertVO = new PayCertVO(payCert.getPayCert(), payCert.getCreateTime());
                    payCertVOList.add(payCertVO);
                }
            }

            purchasePayDetailVO.setCertList(payCertVOList);
            purchasePayDetailVO.setPayAmount(purchaseOrderPay.getPayAmount());
            purchasePayDetailVO.setComment(purchaseOrderPay.getComment());

            // 加入确认的商品详情

            List<PurchasePayDetail> detailList = new ArrayList<>();

            for (PurchasePayDetail purchasePayDetail : purchasePayDetailList) {
                if (purchaseOrderPay.getPayId().equals(purchasePayDetail.getPayId())) {
                    detailList.add(purchasePayDetail);
                }
            }

            purchasePayDetailVO.setPurchasePayDetailList(detailList);

            purchaseConfirmDetailVOList.add(purchasePayDetailVO);

        }

        return ResultVOUtil.success(purchaseConfirmDetailVOList);
    }


    /**
     * 完结采购订单
     */
    @GetMapping("/finish")
    public ResultVO finish(String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        purchaseOrderService.finish(orderId);

        return ResultVOUtil.success();
    }

    /**
     * 确认采购订单
     * @param orderId
     * @return
     */
    @GetMapping("/confirm")
    public ResultVO confirm(@RequestParam("orderId") String orderId, @RequestParam("certUrls") String certUrls) {
        if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(certUrls)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        purchaseOrderService.verify(orderId, certUrls);

        return ResultVOUtil.success();
    }

    @GetMapping("/findConfirmCert")
    public ResultVO findConfirmCert(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        List<VerifyCert> verifyCertList = verifyCertService.findByOrderId(orderId);
//        List<String> certUrlList = verifyCertList.stream().map(e -> e.getCertUrl()).collect(Collectors.toList());

        return ResultVOUtil.success(verifyCertList);
    }

    @GetMapping("/finishConfirm")
    public ResultVO finishConfirm(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderService.findByOrderId(orderId);
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }

        if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.ACCESSED_PART.getCode())) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_STATUS_ERROR);
        }
        purchaseOrderMaster.setOrderStatus(PurchaseOrderStatusEnum.ACCESSED.getCode());
        purchaseOrderService.save(purchaseOrderMaster);

        return ResultVOUtil.success();
    }


    @GetMapping("/finishPay")
    public ResultVO finishPay(@RequestParam("orderId")String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderService.findByOrderId(orderId);
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }

        if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.PAID_PART.getCode())) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_STATUS_ERROR);
        }
        purchaseOrderMaster.setOrderStatus(PurchaseOrderStatusEnum.PAID.getCode());
        purchaseOrderService.save(purchaseOrderMaster);

        return ResultVOUtil.success();
    }

    /**
     * 按照采购订单的编号或者供货商名称，和订单状态 分页查询
     * @param criteria
     * @param orderStatus
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/orderList")
    public ResultVO orderList(@RequestParam(value = "criteria") String criteria,
                              @RequestParam(value = "orderStatus") Integer orderStatus,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (criteria == null) {
            criteria = "";
        }

        List<Integer> orderStatusList = new ArrayList<>();
        // 默认查询除了取消的其他所有状态的订单
        if (orderStatus == null) {
            orderStatusList = Arrays.asList(PurchaseOrderStatusEnum.NEW.getCode(), PurchaseOrderStatusEnum.ACCESSED.getCode(),
                    PurchaseOrderStatusEnum.PAID.getCode(), PurchaseOrderStatusEnum.PAID_PART.getCode(),
                    PurchaseOrderStatusEnum.ACCESSED_PART.getCode(), PurchaseOrderStatusEnum.CONFIRMED.getCode(),
                    PurchaseOrderStatusEnum.FINISHED.getCode());
        } else {
            orderStatusList.add(orderStatus);
        }

        Sort sort = new Sort(Sort.Direction.DESC, "create_time");

        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);

        Page<PurchaseOrderMaster> purchaseOrderMasterPage = purchaseOrderService.findByCriteria(criteria, orderStatusList, pageRequest);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", purchaseOrderMasterPage.getContent());
        resultMap.put("totalPage", purchaseOrderMasterPage.getTotalPages());

        return ResultVOUtil.success(resultMap);
    }

}
