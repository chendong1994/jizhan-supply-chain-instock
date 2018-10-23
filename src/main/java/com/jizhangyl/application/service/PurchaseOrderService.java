package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.PurchaseConfirmDetail;
import com.jizhangyl.application.dataobject.PurchaseOrderDetail;
import com.jizhangyl.application.dataobject.PurchaseOrderMaster;
import com.jizhangyl.application.dataobject.PurchasePayDetail;
import com.jizhangyl.application.dto.PurchaseOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDto create(PurchaseOrderDto purchaseOrderDto);

    Page<PurchaseOrderMaster> findAll(Pageable pageable);

    PurchaseOrderMaster findByOrderId(String orderId);

    Page<PurchaseOrderMaster> findByroviderName(String providerName, Pageable pageable);

    List<PurchaseOrderDetail> findDetailByOrderId(String orderId);

    List<PurchaseOrderDetail> findDetailByOrderIdIn(List<String> orderIdList);

    Page<PurchaseOrderDetail> findDetailByOrderIdInAndProductId(List<String> orderIdList, Integer productId, Pageable pageable);

    void cancel(String orderId);

    void paid(String orderId, String realAmount);

    PurchaseOrderDto confirm(PurchaseOrderDto purchaseOrderDto);

    PurchaseOrderDto pay(PurchaseOrderDto purchaseOrderDto);

    Page<PurchaseOrderDetail> findOrderById(String orderId, Pageable pageable);

    List<PurchaseOrderMaster> findByOrderStatus(Integer orderStatus);

    List<PurchaseConfirmDetail> findAllConfirmDetail();

    List<PurchaseConfirmDetail> findConfirmDetailByConfirmIdIn(List<String> confirmIdList);

    List<PurchasePayDetail> findPayDetailByPayIdIn(List<String> payIdList);

    void finish(String orderId);

    PurchaseOrderMaster save(PurchaseOrderMaster purchaseOrderMaster);

    List<PurchaseOrderMaster> findByCreateTimeBetween(Date beginTime, Date endTime);

    void verify(String orderId, String certUrls);

    PurchaseOrderDto update(PurchaseOrderDto purchaseOrderDto);

    void deletePurchaseOrder(String orderId);

    void deletePurchaseOrderDetailByOrderId(String orderId);

    Page<PurchaseOrderMaster> findByCriteria(String criteria, List<Integer> statusList, Pageable pageable);
}
