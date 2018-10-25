package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.OrderDetail;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/23 10:45
 * @description
 */
public interface OrderDetailService {

    List<OrderDetail> findByOrderId(String orderId);

    List<OrderDetail> findByOrderIdIn(List<String> orderIdList);
}
