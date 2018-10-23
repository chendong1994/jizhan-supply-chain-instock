package com.jizhangyl.application.wechat;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.config.WechatAccountConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/3 12:03
 * @description
 */
@Component
public class WechatConfigTest extends MainApplicationTests {

    @Autowired
    private WechatAccountConfig wechatAccountConfig;

    @Test
    public void test() {
        Assert.assertNotNull(wechatAccountConfig.getMpAppId());
        Assert.assertNotNull(wechatAccountConfig.getMpAppSecret());
    }

}