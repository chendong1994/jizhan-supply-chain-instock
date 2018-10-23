package com.jizhangyl.application.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/11 20:08
 * @description
 */
@Component
public class WechatPayConfig {

    @Autowired
    private WechatAccountConfig accountConfig;

    /**
     * binary wang sdk config
     */
    @Bean
    public WxPayService wxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig());
        return wxPayService;
    }

    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(StringUtils.trimToNull(accountConfig.getMpAppId()));
        wxPayConfig.setMchId(StringUtils.trimToNull(accountConfig.getMchId()));
        wxPayConfig.setMchKey(StringUtils.trimToNull(accountConfig.getMchKey()));
        wxPayConfig.setKeyPath(StringUtils.trimToNull(accountConfig.getKeyPath()));
        wxPayConfig.setNotifyUrl(StringUtils.trimToNull(accountConfig.getNotifyUrl()));
        return wxPayConfig;
    }
}
