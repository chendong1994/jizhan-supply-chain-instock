package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.ProviderProductRelat;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/27 10:47
 * @description
 */
@Component
public class ProviderProductRelatRepositoryTest extends MainApplicationTests {

    @Autowired
    private ProviderProductRelatRepository providerProductRelatRepository;

    @Test
    public void findProvidersProduct() {
        Integer providerId = 15;
        String param = "K";
        List<ProviderProductRelat> providerProductRelatList = providerProductRelatRepository.findProvidersProduct(providerId, param);
        Assert.assertNotEquals(0, providerProductRelatList.size());
    }
}