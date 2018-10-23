package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.PurchaseOrderPay;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 12:15
 * @description
 */
public interface PurchaseOrderPayService {
    List<PurchaseOrderPay> findByOrderId(String orderId);
}
