package com.jizhangyl.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/8 13:16
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "address-resolve")
public class AddressResolveConfig {

    private String host;

    private String path;

    private String appId;

    private String apiKey;
}
