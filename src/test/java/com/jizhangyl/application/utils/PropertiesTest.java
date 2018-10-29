package com.jizhangyl.application.utils;

import com.jizhangyl.application.MainApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/10/29 17:04
 * @description
 */
@Component
public class PropertiesTest extends MainApplicationTests {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${notify-interval}")
    private Integer notifyInterval;

    @Test
    public void test() {
        Assert.assertNotEquals("/", contextPath);
        Assert.assertNotNull(notifyInterval);
        System.out.println("over...");
    }
}
