package com.jizhangyl.application.service.impl;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.repository.OrderMasterRepository;

@Component
public class OrderMasterRepositoryTest extends MainApplicationTests {

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Test
    public void findByOrderId() {
        
        List<OrderMaster> orderList = orderMasterRepository.findByOrderId("1536561425928177705");
        System.out.println();
        System.out.println();
        System.out.println(orderList);
        System.out.println();
        System.out.println();
    }
    
}
