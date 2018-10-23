package com.jizhangyl.application.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.OrderDetail;

/**
 * @author 杨贤达
 * @date 2018/8/10 11:25
 * @description 订单详情表dao
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    List<OrderDetail> findByOrderId(String orderId);

    List<OrderDetail> findByOrderIdIn(List<String> orderIdList);

    Page<OrderDetail> findByOrderIdInAndProductId(List<String> orderIdList, String productId, Pageable pageable);

    /**
     * 查询指定时间段内某个商品的所有订单明细列表
     * @return
     */
    List<OrderDetail> findByProductIdAndCreateTimeBetween(String productId, Date beginDate, Date endDate);
}
