package com.jizhangyl.application.repository;

import com.jizhangyl.application.deprecated.PurchaseOrder2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 9:53
 * @description
 */
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder2, String> {

    List<PurchaseOrder2> findByOrderId(String orderId);

    List<PurchaseOrder2> findByProviderId(Integer providerId);

    Page<PurchaseOrder2> findByProviderIdAndOrderId(Integer providerId, String orderId, Pageable pageable);

    Page<PurchaseOrder2> findByProviderId(Integer providerId, Pageable pageable);

    Page<PurchaseOrder2> findByOrderId(String orderId, Pageable pageable);

}
