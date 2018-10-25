package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderPay;
import com.jizhangyl.application.repository.primary.PurchaseOrderPayRepository;
import com.jizhangyl.application.service.PurchaseOrderPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 14:12
 * @description
 */
@Service
public class PurchaseOrderPayServiceImpl implements PurchaseOrderPayService {

    @Autowired
    private PurchaseOrderPayRepository purchaseOrderPayRepository;

    @Override
    public List<PurchaseOrderPay> findByOrderId(String orderId) {
        return purchaseOrderPayRepository.findByOrderId(orderId);
    }
}
