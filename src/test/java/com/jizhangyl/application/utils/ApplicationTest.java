package com.jizhangyl.application.utils;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.service.ProductProviderService;
import org.junit.Test;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/10/26 10:53
 * @description
 */
@Component
public class ApplicationTest extends MainApplicationTests {

    @Test
    public void getBean() {
        ProductProviderService productProviderService = Application.getBean(ProductProviderService.class);
        System.out.println(productProviderService);
    }
}