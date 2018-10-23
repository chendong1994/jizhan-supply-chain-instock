package com.jizhangyl.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/18 11:28
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    private String bucketUrl;
}
