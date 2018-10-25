package com.jizhangyl.application.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.repository.primary.OrderDetailRepository;
import com.jizhangyl.application.service.OrderDetailService;

/**
 * @author 杨贤达
 * @date 2018/8/23 10:46
 * @description
 */
@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetail> findByOrderId(String orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> findByOrderIdIn(List<String> orderIdList) {
        return orderDetailRepository.findByOrderIdIn(orderIdList);
    }
}
