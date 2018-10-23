package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.PurchaseOrderConfirm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 11:05
 * @description
 */
public interface PurchaseOrderConfirmRepository extends JpaRepository<PurchaseOrderConfirm, String> {

    List<PurchaseOrderConfirm> findByOrderId(String orderId);
}
