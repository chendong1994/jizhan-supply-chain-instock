package com.jizhangyl.application.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaUserServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/10/15 10:35
 * @description
 */
@Component
public class WechatMaConfig {

    @Autowired
    private WechatAccountConfig accountConfig;

    @Bean
    public WxMaUserService wxMaUserService() {
        WxMaUserService wxMaUserService = new WxMaUserServiceImpl(wxMaService());
        return wxMaUserService;
    }

    @Bean
    public WxMaService wxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig());
        return wxMaService;
    }

    @Bean
    public WxMaConfig wxMaConfig() {
        WxMaInMemoryConfig wxMaConfig = new WxMaInMemoryConfig();
        wxMaConfig.setAppid(accountConfig.getMpAppId());
        wxMaConfig.setSecret(accountConfig.getMpAppSecret());
        return wxMaConfig;
    }
}
