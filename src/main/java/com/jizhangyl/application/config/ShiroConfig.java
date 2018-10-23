package com.jizhangyl.application.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jizhangyl.application.security.ShiroSessionManager;
import com.jizhangyl.application.security.ShiroRealm;

/**
 * @author 曲健磊
 * @date 2018年9月23日 下午2:09:14
 * @description shiro配置
 */
//@Configuration
public class ShiroConfig {

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        
        Map<String, String> filterMap = new LinkedHashMap<>();
        // 配置退出过滤器,具体的代码,shiro已经替我们实现了
        filterMap.put("/user/logout", "logout");
        // 配置匿名访问请求
        filterMap.put("/1810081340/**", "anon");
        filterMap.put("/common/**", "anon");
        filterMap.put("/config/**", "anon");
        filterMap.put("/user/**", "anon");
        filterMap.put("/wechat/**", "anon"); // 对微信扫码登录不要拦截<<=========================
        filterMap.put("/**", "authc");
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        // 配置shiro的默认登录界面的地址,前后端分离中登录界面跳转应有前端路由控制,后台仅返回json数据
        shiroFilter.setLoginUrl("/user/unauth");
        
        return shiroFilter;
    }
    
    @Bean
    public ShiroRealm myShiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        return shiroRealm;
    }
    
    @Bean("securityManager")
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
//        securityManager.setSessionManager(sessionManager());
//        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }
    
    @Bean("sessionManager")
    public SessionManager sessionManager(){
        ShiroSessionManager mySessionManager = new ShiroSessionManager();
//        mySessionManager.setSessionDAO(redisSessionDAO());
        return mySessionManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
