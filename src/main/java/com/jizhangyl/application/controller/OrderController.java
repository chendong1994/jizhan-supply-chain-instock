package com.jizhangyl.application.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jizhangyl.application.VO.OrderMasterVO;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.constant.CookieConstant;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.converter.OrderForm2OrderDto;
import com.jizhangyl.application.converter.OrderMaster2InstockOrderDTOConverter;
import com.jizhangyl.application.converter.OrderMaster2OrderForRepositoryConverter;
import com.jizhangyl.application.dataobject.primary.BuyerInfo;
import com.jizhangyl.application.dataobject.primary.NotifyLog;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderForRepository;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.dto.InstockOrderDTO;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.dto.QueryOrderDetailReceiverDTO;
import com.jizhangyl.application.dto.QueryOrderDetailShopDTO;
import com.jizhangyl.application.dto.QueryOrderMasterDTO;
import com.jizhangyl.application.dto.SenderDTO;
import com.jizhangyl.application.entity.PageEntity;
import com.jizhangyl.application.entity.PageParam;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.OrderForm;
import com.jizhangyl.application.service.BuyerService;
import com.jizhangyl.application.service.ExportService;
import com.jizhangyl.application.service.NotifyLogService;
import com.jizhangyl.application.service.OrderDetailService;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.QueryOrderService;
import com.jizhangyl.application.service.WxuserAddrService;
import com.jizhangyl.application.service.WxuserService;
import com.jizhangyl.application.service.impl.OrderImportServiceImpl;
import com.jizhangyl.application.utils.CookieUtil;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.PageUtils;
import com.jizhangyl.application.utils.ResultVOUtil;
import com.jizhangyl.application.utils.SmsUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨贤达
 * @date 2018/8/10 16:13
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Value("${notify-interval:5}")
    private int notifyInterval;

    @Autowired
    private OrderMasterService orderService;

    @Autowired
    private OrderDetailService orderDetailService;
    
    @Autowired
    private QueryOrderService queryOrderService;
    
    @Autowired
    private ExportService exportService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private WxuserAddrService wxuserAddrService;
    
    @Autowired
    private BuyerService buyerService;
    
    @Autowired
    private OrderImportServiceImpl orderImportService;
    
    @Autowired
    private OrderMaster2OrderForRepositoryConverter orderMaster2OrderForRepositoryConverter;

    @Autowired
    private WxuserService wxuserService;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private NotifyLogService notifyLogService;

    @Autowired
    private OrderMaster2InstockOrderDTOConverter orderMaster2InstockOrderDTOConverter;
    
    @PostMapping("/create")
    public ResultVO create(@Valid OrderForm orderForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm = ", orderForm);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        OrderDto orderDto = OrderForm2OrderDto.convert(orderForm);
        if (CollectionUtils.isEmpty(orderDto.getOrderDetailList())) {
            log.error("【创建订单】购物车不能为空");
            throw new GlobalException(ResultEnum.CART_EMPTY);
        }

        OrderDto createResult = orderService.create(orderDto, false);

        Map<String, String> map = new HashMap<>();
        map.put("orderId", createResult.getOrderId());

        return ResultVOUtil.success(map);
    }

    @GetMapping("/findByOrderStatus")
    public ResultVO findByBuyerOpenidAndOrderStatus(String openid, Integer orderStatus,
                                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (StringUtils.isEmpty(openid) || orderStatus == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<OrderDto> orderDtoList = orderService.findByBuyerOpenidAndOrderStatus(openid, orderStatus, page, size);
        return ResultVOUtil.success(orderDtoList);
    }

    @GetMapping("/sellAmount")
    public ResultVO sellAmount(String openid, String beginDate, String endDate) {
        if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endDate)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        Map<String, String> map = orderService.sellAmount(openid, beginDate, endDate);
        return ResultVOUtil.success(map);
    }

    @GetMapping("/cancel")
    public ResultVO cancel(@RequestParam("openid") String openid,
                           @RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        try {
            OrderDto orderDto = orderService.findOne(orderId);
            orderService.cancel(orderDto);
        } catch (Exception e) {
            log.error("【用户取消订单】发生异常: {}", e);
            throw new GlobalException(ResultEnum.ORDER_CANCEL_ERROR.getCode(), String.format(ResultEnum.ORDER_CANCEL_ERROR.getMessage(), e.getMessage()));
        }
        return ResultVOUtil.success(ResultEnum.ORDER_CANCEL_SUCCESS.getMessage());

    }

    // 订单发货
    @GetMapping("/delivery")
    public ResultVO delivery(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        orderService.delivery(orderId);
        return ResultVOUtil.success();
    }

    /**
     * 根据"物流单号,航班提单号,买手邀请码"以及"订单状态"查询订单列表
     * @return
     */
    @GetMapping("/findByExpressDeliveryInviteCodeStatus")
    public ResultVO findByExpressDeliveryInviteCodeStatus(
            @RequestParam(value = "content", required = true) String content,
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            String startTimeStr, String endTimeStr) {

        // 查询出全部数据
        List<QueryOrderMasterDTO> orderList = queryOrderService.findByExpressDeliveryInviteCodeStatus(content, status, startTimeStr, endTimeStr, page, size);
        
        PageParam pageParam = new PageParam(page - 1, size);
        PageEntity<QueryOrderMasterDTO> pageEntity = PageUtils.startPage(orderList, pageParam);
        
        
        // 总页数
//        Integer totalpage = orderList.size() / size + 1;
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("orderList", pageEntity.getPageList());
        result.put("totalPage", pageEntity.getTotalPages());
        result.put("totalItems", pageEntity.getTotalElements());
//        result.put("orderList", orderList.subList((page - 1) * size, page * size > orderList.size() ? orderList.size() : page * size));
//        result.put("totalPage", totalpage);
//        result.put("totalItems", orderList.size());
        return ResultVOUtil.success(result);
    }
    
    /**
     * 根据物流单号查询物流动态
     * @param expNum
     * @return
     */
    @GetMapping("/getLofindLogisticsStatusByExpNum")
    public ResultVO getLofindLogisticsStatusByExpNum(String expNum) {
        List<String> traces = queryOrderService.findLogisticsStatus(expNum);
        return ResultVOUtil.success(traces);
    }
    
    /**
     * 查出当天的所有的订单列表
     * @return
     */
    @GetMapping("/flushOrderList")
    public ResultVO flushOrderList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = DateUtil.initDateByDay(); // 当天零时零分零秒
        Date endTime = new Date(); // 这一时刻
        String startTimeStr = sdf.format(startTime);
        String endTimeStr = sdf.format(endTime);
        
        // 查询出全部数据
        List<QueryOrderMasterDTO> orderList = queryOrderService.findByExpressDeliveryInviteCodeStatus("", OrderStatusEnum.FIND_ALL.getCode().toString(), startTimeStr, endTimeStr, page, size);
        
        PageParam pageParam = new PageParam(page - 1, size);
        PageEntity<QueryOrderMasterDTO> pageEntity = PageUtils.startPage(orderList, pageParam);
        
        // 总页数
//        Integer totalpage = orderList.size() / size + 1;
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("orderList", pageEntity.getPageList());
        result.put("totalPage", pageEntity.getTotalPages());
        result.put("totalItems", pageEntity.getTotalElements());
//        result.put("orderList", orderList.subList((page - 1) * size, page * size > orderList.size() ? orderList.size() : page * size));
//        result.put("totalPage", totalpage);
//        result.put("totalItems", orderList.size());
        return ResultVOUtil.success(result);
    }
    
    /**
     * 根据订单id查询订单详情
     * @param orderId 订单id
     * @return 商品详情列表+收件人信息
     */
    @GetMapping("/findOrderDetailByOrderId")
    public ResultVO findOrderDetailByOrderId(String orderId) {
        // 1.根据orderId查询出商品详情列表
        List<QueryOrderDetailShopDTO> orderDetailList = queryOrderService.findOrderDetailByOrderId(orderId);
        // 2.根据orderId查询出收件人信息
        List<QueryOrderDetailReceiverDTO> reveiverList = queryOrderService.findOrderReceiverByOrderId(orderId);
        // 3.根据orderId查出发件人信息(发件人名称,发件人电话)
        List<SenderDTO> senderList = queryOrderService.findOrderSenderByOrderId(orderId);

        Map<String, List> detailMap = new HashMap<String, List>();
        detailMap.put("shopList", orderDetailList);
        detailMap.put("reveiverList", reveiverList);
        detailMap.put("senderList", senderList);

        return ResultVOUtil.success(detailMap);
    }
    
    /**
     * 导出指定时间段内的订单
     * @param beginDate 开始时间
     * @param endDate 截止时间
     * @return excel文件
     */
    @GetMapping("/exportOrderDetail")
    public ResponseEntity<byte[]> exportOrderDetail(String startTimeStr,
                                                    String endTimeStr,
                                                    Integer status,
                                                    HttpServletRequest request) {
        // 1.开始日期和截止日期都为空则默认导出当天的订单
        if (StringUtils.isEmpty(startTimeStr) && StringUtils.isEmpty(endTimeStr)) {
            String openid = null;
            Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
            if (cookie != null && !StringUtils.isEmpty(cookie.getValue())) {
                openid = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date startTime = DateUtil.initDateByDay(); // 开始时间:当天0点
                Date endTime = new Date();  // 结束时间:当前

                List<OrderMaster> orderMasterList = orderService.findByCreateTimeBetween(startTime, endTime);
                
                if (status != null && !OrderStatusEnum.FIND_ALL.getCode().equals(status)) { // 如果前台传递的订单状态不为空,则进行筛选
                    orderMasterList = orderMasterList.stream().filter(e -> {
                        if (e.getOrderStatus().equals(status)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());
                }
                
                List<Integer> recvAddrIdList = orderMasterList.stream().map(e -> e.getRecipientAddrId()).collect(Collectors.toList());

                List<WxuserAddr> wxuserAddrList = wxuserAddrService.findByIdIn(recvAddrIdList);

                BuyerInfo buyerInfo = buyerService.findBuyerInfo(orderMasterList);

                List<OrderForRepository> orderForRepositoryList = orderMaster2OrderForRepositoryConverter.convertAll(orderMasterList, wxuserAddrList, buyerInfo);

                log.info("orderForRepositoryList = {}", orderForRepositoryList);

                return exportService.exportOrder(orderForRepositoryList, startTime, endTime);
            } catch (Exception e) {
                e.printStackTrace();
                throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
            }
        }

        // 2.开始日期和截止日期有一个为空的情况
        if (StringUtils.isEmpty(startTimeStr) || StringUtils.isEmpty(endTimeStr)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        // 3.开始日期和截止日期都不为空的情况
        String openid = null;
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie != null && !StringUtils.isEmpty(cookie.getValue())) {
            openid = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startTime = sdf.parse(org.apache.commons.lang3.StringUtils.trimToNull(startTimeStr));
            Date endTime = sdf.parse(org.apache.commons.lang3.StringUtils.trimToNull(endTimeStr));

            List<OrderMaster> orderMasterList = orderService.findByCreateTimeBetween(startTime, DateUtil.add(endTime, 1));

            if (status != null && !OrderStatusEnum.FIND_ALL.getCode().equals(status)) { // 如果前台传递的订单状态不为空,则进行筛选
                orderMasterList = orderMasterList.stream().filter(e -> {
                    if (e.getOrderStatus().equals(status)) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
            }
            
            List<Integer> recvAddrIdList = orderMasterList.stream().map(e -> e.getRecipientAddrId()).collect(Collectors.toList());

            List<WxuserAddr> wxuserAddrList = wxuserAddrService.findByIdIn(recvAddrIdList);

            BuyerInfo buyerInfo = buyerService.findBuyerInfo(orderMasterList);

            List<OrderForRepository> orderForRepositoryList = orderMaster2OrderForRepositoryConverter.convertAll(orderMasterList, wxuserAddrList, buyerInfo);

            log.info("orderForRepositoryList = {}", orderForRepositoryList);

            return exportService.exportOrder(orderForRepositoryList, startTime, endTime);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
        }
    }

    /**
     * 需要短信通知的订单列表（按照顺序展示，不能发送短信的为灰色的）
     * @return
     */
    @GetMapping("/notifyList")
    public ResultVO notifyList() {

        List<OrderMaster> orderMasterList = orderService.findByOrderStatusOrderByLastNotifyTime(OrderStatusEnum.PAID.getCode());

        List<String> openidList = orderMasterList.stream().map(e -> e.getBuyerOpenid()).collect(Collectors.toList());

        List<Wxuser> wxuserList = wxuserService.findByOpenIdIn(openidList);

        List<OrderMasterVO> orderMasterVOList = new ArrayList<>();

        orderMasterVOList = orderMasterList.stream().map(e -> {
            OrderMasterVO orderMasterVO = new OrderMasterVO();
            BeanUtils.copyProperties(e, orderMasterVO);

            for (Wxuser wxuser : wxuserList) {
                if (wxuser.getOpenId().equals(e.getBuyerOpenid())) {
                    orderMasterVO.setBuyerInviteCode(wxuser.getInviteCode());
                    break;
                }
            }

            if (e.getLastNotifyTime() == null) {
                orderMasterVO.setStatus(true);
                orderMasterVO.setNotifyCount(0); // 没有发送提醒则短信提醒次数为0
            } else {
                Date lastNotifyTime = e.getLastNotifyTime();
                long interval = (System.currentTimeMillis() - lastNotifyTime.getTime()) / 1000;
                if (interval > 86400 * notifyInterval) {
                    orderMasterVO.setStatus(true);
                } else {
                    orderMasterVO.setStatus(false);
                }
                orderMasterVO.setNotifyCount(notifyLogService.countByOrderId(orderMasterVO.getOrderId()));
            }
            // 添加统计短信提醒的次数
            orderMasterVO.setNotifyCount(notifyLogService.countByOrderId(orderMasterVO.getOrderId()));
            return orderMasterVO;
        }).collect(Collectors.toList());

        return ResultVOUtil.success(orderMasterVOList);
    }

    @GetMapping("/orderNotify")
    public ResultVO orderNotify(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        OrderDto orderDto = orderService.findOne(orderId);
        if (orderDto == null) {
            throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
        }
        String expressNum = orderDto.getExpressNumber();
        String recipientPhone = orderDto.getRecipientPhone();
        if (StringUtils.isEmpty(expressNum) || StringUtils.isEmpty(recipientPhone)) {
            log.error("【证件上传提醒】expressNum = {}, recipientPhone = {}", expressNum, recipientPhone);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(),
                    "expressNum = " + expressNum + ", recipientPhone = " + recipientPhone));
        }

        boolean flag = false;
        Date lastNotifyTime = orderDto.getLastNotifyTime();
        NotifyLog notifyLog = new NotifyLog();
        if (lastNotifyTime == null) {
            flag = true;
            smsUtil.orderNotify(recipientPhone, expressNum, orderDto.getOrderId());
            // 记录短信提醒日志
            notifyLog.setOrderId(orderId);
            notifyLog.setExpressNumber(expressNum);
            notifyLog.setRecipientPhone(recipientPhone);
            notifyLogService.saveNotifyLog(notifyLog);
        } else {
            long interval = (System.currentTimeMillis() - lastNotifyTime.getTime()) / 1000;
            if (interval > 86400 * notifyInterval) {
                flag = true;
                smsUtil.orderNotify(recipientPhone, expressNum, orderDto.getOrderId());
                // 记录短信提醒日志
                notifyLog.setOrderId(orderId);
                notifyLog.setExpressNumber(expressNum);
                notifyLog.setRecipientPhone(recipientPhone);
                notifyLogService.saveNotifyLog(notifyLog);
            }
        }

        if (flag) {
            // 更新本次提醒时间到订单表
            OrderMaster orderMaster = new OrderMaster();
            BeanUtils.copyProperties(orderDto, orderMaster);
            orderMaster.setLastNotifyTime(new Date());
            orderService.save(orderMaster);
        }

        return ResultVOUtil.success();
    }

    /**
     * 根据订单号查询订单详情
     */
    @GetMapping("/detail")
    public ResultVO detail(@RequestParam("orderId") String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        OrderDto orderDto = orderService.findOne(orderId);
        return ResultVOUtil.success(orderDto);
    }

    /**
     * 按照物流单号查询所有的订单记录
     * @param expressNum
     * @return
     */
    @GetMapping("/findDetailByExpressNum")
    public ResultVO findDetailByExpressNum(@RequestParam("expressNum") String expressNum) {
        if (StringUtils.isEmpty(expressNum)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        // 结果集
        OrderDto orderDto = new OrderDto();

        OrderMaster orderMaster = orderService.findByExpressNumberAndOrderStatusNotIn(expressNum,
                Arrays.asList(OrderStatusEnum.CANCELED.getCode()));

        if (orderMaster == null) {
            throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
        }

        List<OrderDetail> orderDetailList = orderDetailService.findByOrderId(orderMaster.getOrderId());

        BeanUtils.copyProperties(orderMaster, orderDto);
        orderDto.setOrderDetailList(orderDetailList);

        return ResultVOUtil.success(orderDto);
    }
    
    /**
     * 导出现货的订单(待发货)
     * @return
     */
    @GetMapping("/exportInstockOrder")
    public ResponseEntity<byte[]> exportInstockOrder() {
        // 只导出待发货的订单
        Integer orderStatus = OrderStatusEnum.PAID.getCode();
        
        try {
            List<OrderMaster> orderMasterList = orderService.findByOrderStatus(orderStatus);

            List<InstockOrderDTO> instockOrderDTOList = orderMaster2InstockOrderDTOConverter.convert(orderMasterList);

            return exportService.exportInstockOrder(instockOrderDTOList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
        }
    }
}
