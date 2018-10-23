package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.PurchaseOrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/20 16:27
 * @description
 */
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, String> {

    List<PurchaseOrderDetail> findByOrderId(String orderId);

    List<PurchaseOrderDetail> findByOrderIdIn(List<String> orderIdList);

    Page<PurchaseOrderDetail> findByOrderIdInAndProductId(List<String> orderIdList, Integer productId, Pageable pageable);

    Page<PurchaseOrderDetail> findByOrderId(String orderId, Pageable pageable);

    List<PurchaseOrderDetail> findByDetailIdIn(List<String> detailIdList);

    void deleteByOrderId(String orderId);
}
