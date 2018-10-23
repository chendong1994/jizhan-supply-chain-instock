package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.jizhangyl.application.dataobject.ExpressNumJp;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.PayTypeEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.ExpressNumJpService;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.PayService;
import com.jizhangyl.application.utils.IpUtil;
import com.jizhangyl.application.utils.JsonUtil;
import com.jizhangyl.application.utils.MathUtil;
import com.jizhangyl.application.utils.PriceUtil;
import com.jizhangyl.application.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨贤达
 * @date 2018/8/11 21:28
 * @description
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {

    private static final String BODY = "微信支付";

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private OrderMasterService orderService;

    @Autowired
    private ExpressNumJpService expressNumJpService;

    @Autowired
    private SmsUtil smsUtil;

    @Override
    public WxPayUnifiedOrderRequest create(OrderDto orderDto, HttpServletRequest request) {
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setBody(BODY);
            // 设置随机字符串
//            orderRequest.setNonceStr(PayUtil.getNonceStr());
            orderRequest.setOpenid(orderDto.getBuyerOpenid());
            // outTradeNo -> 订单号
            orderRequest.setOutTradeNo(orderDto.getOrderId());
            // 用户IP地址
            orderRequest.setSpbillCreateIp(IpUtil.getIp(request));
            // 金额为整数，单位为分
            // TODO 用于测试，改为 1
            orderRequest.setTotalFee(PriceUtil.yuanToFee(orderDto.getOrderAmount()));
//            orderRequest.setTotalFee(1);
            // tradeType -> 支付方式
            orderRequest.setTradeType(WxPayConstants.TradeType.JSAPI);
//            return wxPayService.createOrder(orderRequest);
            return orderRequest;
        } catch (Exception e) {
            log.error("【微信支付】支付失败，订单号 = {}, 原因 = {}", orderDto.getOrderId(), e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    @Override
    public WxPayOrderNotifyResult notify(String notifyData) {

        // 1. 验证签名
        // 2. 支付的状态
        // 3. 支付金额
        // 4. 支付人（下单人 == 支付人）
        WxPayOrderNotifyResult notifyResult = null;
        try {
            notifyResult = wxPayService.parseOrderNotifyResult(notifyData);
            log.info("【微信支付】异步通知，notifyResult = {}", JsonUtil.toJson(notifyResult));
            // 查询订单
            OrderDto orderDto = orderService.findOne(notifyResult.getOutTradeNo());

            // 判断订单是否存在
            if (orderDto == null) {
                log.error("【微信支付】异步通知，订单不存在，orderId = {}", notifyResult.getOutTradeNo());
                throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
            }

            // 判断金额是否一致
            Integer totalFee = PriceUtil.yuanToFee(orderDto.getOrderAmount());
            // TODO, 改为 1 用于测试
            if (!MathUtil.equals(notifyResult.getTotalFee(), totalFee)) {
//            if (!MathUtil.equals(notifyResult.getTotalFee(), 1)) {
                log.error("【微信支付】异步通知，订单金额不一致，orderId = {}, 微信通知金额 = {}, 系统金额 = {}",
                        notifyResult.getOutTradeNo(), notifyResult.getTotalFee(), totalFee);
                throw new GlobalException(ResultEnum.WXPAY_NOTIFY_MONEY_VERIFY_ERROR);
            }

            // 修改订单状态
            orderService.paid(orderDto);

            // 生成物流单号
            ExpressNumJp unusedNum = expressNumJpService.findUnused();
            orderDto.setExpressNumber(unusedNum.getExpNum());

            // 保存单号至订单主表
            OrderMaster orderMaster = new OrderMaster();
            BeanUtils.copyProperties(orderDto, orderMaster);
            orderService.save(orderMaster);

            // 单号标记为已经使用
            expressNumJpService.updateStatus(unusedNum.getId(), ExpressNumStatusEnum.USED.getCode());

            try {
                // 触发短信通知买家查询物流
                SendSmsResponse response = smsUtil.expressNotify(orderDto.getRecipientPhone(), unusedNum.getExpNum(), null);
                if (response.getCode() != null && response.getCode().equals("OK")) {
                    log.warn("【微信支付】提醒短信下发成功");
                } else {
                    log.warn("【微信支付】提醒短信下发失败");
                }
            } catch (Exception e) {
                log.error("【物流单号】单号下发用户失败, {}", e);
            }

        } catch (WxPayException e) {
            log.error("【微信支付】异步通知异常：{}", e.getMessage());
            e.printStackTrace();
        }
        return notifyResult;
    }

    /**
     * 退款
     * @param orderDto
     */
    @Override
    public WxPayRefundResult refund(OrderDto orderDto) {
        WxPayRefundResult refundResult = null;
        try {
            WxPayRefundRequest refundRequest = new WxPayRefundRequest();
            refundRequest.setOutTradeNo(orderDto.getOrderId());
            refundRequest.setOutRefundNo(orderDto.getOrderId());
            // TODO 用于测试，改为一分钱
            refundRequest.setTotalFee(PriceUtil.yuanToFee(orderDto.getOrderAmount()));
//            refundRequest.setTotalFee(1);
            refundRequest.setRefundFee(PriceUtil.yuanToFee(orderDto.getOrderAmount()));
//            refundRequest.setRefundFee(1);
            refundRequest.setRefundFeeType(PayTypeEnum.WX_PAY.getMsg());
            log.info("【微信退款】refundRequest = {}", JsonUtil.toJson(refundRequest));

            // 调用退款 api
            refundResult = wxPayService.refund(refundRequest);
            log.info("【微信退款】refundResult = {}", refundResult);
        } catch (WxPayException e) {
            log.error("【微信退款】异常: {}", e);
            e.printStackTrace();
//            throw new GlobalException(ResultEnum.WECHAT_REFUND_ERROR);
        }
        return refundResult;
    }
}
