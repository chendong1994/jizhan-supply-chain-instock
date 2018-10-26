package com.jizhangyl.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/3 22:46
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "project-url")
public class ProjectUrlConfig {

    private String wechatOpenAuthorize;

    private String jizhangyl;

}
