package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 杨贤达
 * @date 2018/8/10 11:36
 * @description
 */
public interface OrderMasterService {

    void delete(String orderId);

    Page<OrderDto> findAll(Pageable pageable);

    OrderDto create(OrderDto orderDto, boolean fromFile);

    OrderDto findOne(String orderId);

    OrderDto paid(OrderDto orderDto);

    List<OrderDto> findByBuyerOpenidAndOrderStatus(String openid, Integer orderStatus,
                                                   Integer page, Integer size);

    Map<String, String> sellAmount(String openid, String beginDate, String endDate);

    OrderDto cancel(OrderDto orderDto);

    List<OrderMaster> findByOrderIdIn(List<String> orderIdList);

    List<OrderMaster> findByOrderStatusAndDeliveryTimeBetween(Integer orderStatus, Date startTime, Date endTime);

    void delivery(String orderId);

    void delivery(List<OrderMaster> orderMasterList);

    List<OrderMaster> findByExpressNumber(String expressNumber);

    List<OrderMaster> findByExpressNumberIn(List<String> expressNumberList);

    OrderMaster findByExpressNumberAndOrderStatusNotIn(String expressNumber, List<Integer> orderStatusList);

    List<OrderMaster> findByCreateTimeBetween(Date startTime, Date endTime);

    OrderMaster save(OrderMaster orderMaster);

    List<OrderMaster> findByBuyerOrderIdIn(List<String> buyerOrderIdList);

    OrderMaster findByExpressNumberAndOrderStatus(String expressNumber, Integer orderStatus);

    List<OrderMaster> findByBuyerOpenidAndOrderStatusAndUpdateTimeIn(String buyerOpenid, Integer orderStatus, Date beginDate, Date endDate);

    List<OrderMaster> findByOrderStatus(Integer orderStatus);

    List<OrderMaster> findByOrderStatusOrderByLastNotifyTime(Integer orderStatus);

    List<OrderDetail> findDetailByOrderIdIn(List<String> orderIdList);

    Page<OrderDetail> findDetailByOrderIdInAndProductId(List<String> orderIdList, String productId, Pageable pageable);
}
