package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.repository.primary.OrderMasterRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/5 11:54
 * @description
 */
@Component
public class OrderMasterRepositoryTest extends MainApplicationTests {

    @Autowired
    private OrderMasterRepository repository;

    @Test
    public void findByExpressNumberAndOrderStatusNotIn() {
        List<Integer> orderStatusList = Arrays.asList(OrderStatusEnum.NEW.getCode(), OrderStatusEnum.CANCELED.getCode());
        OrderMaster orderMaster = repository.findByExpressNumberAndOrderStatusNotIn("BS057110017CN", orderStatusList);
        Assert.assertNotNull(orderMaster);
    }
}