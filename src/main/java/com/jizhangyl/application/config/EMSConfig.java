package com.jizhangyl.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/24 23:41
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "ems")
public class EMSConfig {

    private String url;

    private String version;

    private String authenticate;
}
