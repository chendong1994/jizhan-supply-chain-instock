package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.service.OrderMasterService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/11 21:40
 * @description
 */
@Component
public class PayServiceImplTest extends MainApplicationTests {

    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private OrderMasterService orderMasterService;

    @Test
    public void create() throws Exception {
        OrderDto orderDto = orderMasterService.findOne("1");
//        payService.create(orderDto);

    }
}