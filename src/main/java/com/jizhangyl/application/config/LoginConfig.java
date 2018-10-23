package com.jizhangyl.application.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/30 10:18
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "login")
public class LoginConfig {

    @Value("${server.context-path:/}")
    private String contextPath;

    private String excludePrefix;

    private String excludeSuffix;

    private String excludePatterns;

    public String[] getLoginExcludePrefix() {
        List<String> loginExcludePrefixList = new ArrayList<>();
        for (String prefix : excludePrefix.split(";")) {
            loginExcludePrefixList.add(contextPath + prefix);
        }
        return loginExcludePrefixList.toArray(new String[1]);
    }

    public String[] getLoginExcludeSuffix() {
        List<String> loginExcludeSuffixList = new ArrayList<>();
        for (String suffix : excludeSuffix.split(";")) {
            loginExcludeSuffixList.add(suffix);
        }
        return loginExcludeSuffixList.toArray(new String[1]);
    }

    public String[] getLoginExcludePatterns() {
        List<String> loginExcludePatternsList = new ArrayList<>();
        for (String pattern : excludePatterns.split(";")) {
            loginExcludePatternsList.add(pattern);
        }
        return loginExcludePatternsList.toArray(new String[1]);
    }
}
