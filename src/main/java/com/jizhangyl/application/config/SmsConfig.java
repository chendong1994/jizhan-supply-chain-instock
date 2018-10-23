package com.jizhangyl.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 杨贤达
 * @date 2018/9/4 11:58
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {

    private String product;

    private String domain;

    private String accessKeyId;

    private String accessKeySecret;

    private String signName;

    private Map<String, String> templateCode;
}
