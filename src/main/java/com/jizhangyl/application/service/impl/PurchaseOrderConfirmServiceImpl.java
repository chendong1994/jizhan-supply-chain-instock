package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderConfirm;
import com.jizhangyl.application.repository.primary.PurchaseOrderConfirmRepository;
import com.jizhangyl.application.service.PurchaseOrderConfirmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 14:12
 * @description
 */
@Service
public class PurchaseOrderConfirmServiceImpl implements PurchaseOrderConfirmService {

    @Autowired
    private PurchaseOrderConfirmRepository purchaseOrderConfirmRepository;

    @Override
    public List<PurchaseOrderConfirm> findByOrderId(String orderId) {
        return purchaseOrderConfirmRepository.findByOrderId(orderId);
    }
}
