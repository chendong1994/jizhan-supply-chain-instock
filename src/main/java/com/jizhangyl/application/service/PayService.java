package com.jizhangyl.application.service;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.jizhangyl.application.dto.OrderDto;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨贤达
 * @date 2018/8/11 21:27
 * @description
 */
public interface PayService {

    WxPayUnifiedOrderRequest create(OrderDto orderDto, HttpServletRequest request);

    WxPayOrderNotifyResult notify(String notifyData);

    WxPayRefundResult refund(OrderDto orderDto);
}
