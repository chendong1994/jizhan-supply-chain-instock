package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.Shop;
import com.sun.xml.internal.ws.policy.AssertionValidationProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author 杨贤达
 * @date 2018/9/27 14:20
 * @description
 */
@Component
public class ShopRepositoryTest extends MainApplicationTests {

    @Autowired
    private ShopRepository shopRepository;

    @Test
    public void findByCriteria() {
        String param = "JZ201808210094";
        List<Shop> shopList = shopRepository.findByCriteria(param);
        Assert.assertNotEquals(0, shopList.size());
    }
}