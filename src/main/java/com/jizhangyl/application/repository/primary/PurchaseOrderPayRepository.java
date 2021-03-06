package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderPay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 11:05
 * @description
 */
public interface PurchaseOrderPayRepository extends JpaRepository<PurchaseOrderPay, String> {

    List<PurchaseOrderPay> findByOrderId(String orderId);
}
