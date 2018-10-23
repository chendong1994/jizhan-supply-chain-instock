package com.jizhangyl.application.config;

import com.jizhangyl.application.MainApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.Assert.*;

/**
 * @author 杨贤达
 * @date 2018/8/30 10:37
 * @description
 */
@Component
public class LoginConfigTest extends MainApplicationTests {

    @Autowired
    private LoginConfig loginConfig;

    @Test
    public void test() {
        String[] excludePrefix = loginConfig.getLoginExcludePrefix();
        String[] excludeSuffix = loginConfig.getLoginExcludeSuffix();
        Assert.assertTrue(excludePrefix.length > 0 && excludeSuffix.length > 0);
    }

}