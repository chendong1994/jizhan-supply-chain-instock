package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.PurchaseOrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/6 15:42
 * @description
 */
public interface PurchaseOrderMasterRepository extends JpaRepository<PurchaseOrderMaster, String> {

    PurchaseOrderMaster findByOrderId(String orderId);

    Page<PurchaseOrderMaster> findByProviderNameLike(String providerName, Pageable pageable);

    List<PurchaseOrderMaster> findByOrderStatus(Integer orderStatus);

    List<PurchaseOrderMaster> findByCreateTimeBetween(Date beginTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from `purchase_order_master` where concat(ifnull(`provider_name`, ''), " +
            "ifnull(`order_id`, '')) like %:criteria% and `order_status` in (:statusList) order by ?#{#pageable}")
    Page<PurchaseOrderMaster> findByCriteria(@Param("criteria") String criteria,
                                             @Param("statusList") List<Integer> statusList,
                                             Pageable pageable);
}
