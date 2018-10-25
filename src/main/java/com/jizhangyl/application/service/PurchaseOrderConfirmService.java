package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderConfirm;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 12:15
 * @description
 */
public interface PurchaseOrderConfirmService {
    List<PurchaseOrderConfirm> findByOrderId(String orderId);
}
