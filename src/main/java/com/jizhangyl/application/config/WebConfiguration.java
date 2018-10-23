package com.jizhangyl.application.config;

import com.jizhangyl.application.constant.CookieConstant;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.exception.AuthorizeException;
import com.jizhangyl.application.exception.MobileException;
import com.jizhangyl.application.utils.CookieUtil;
import com.jizhangyl.application.utils.DevelopPermitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨贤达
 * @date 2018/8/29 22:51
 * @description WebConfiguration
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod")
public class WebConfiguration {

    @Autowired
    private LoginConfig loginConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }

    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new LoginFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("LoginFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 登录过滤器
     */
    public class LoginFilter implements Filter {

        @Override
        public void destroy() { }

        @Override
        public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain filterChain)
                throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) srequest;
            HttpServletResponse response = (HttpServletResponse) sresponse;

            String uri = request.getRequestURI();
            log.info("【登录校验】request uri = {}", uri);

            // 不过滤的前缀
            String[] excludePrefix = loginConfig.getLoginExcludePrefix();

            // 不过滤的后缀
            String[] excludeSuffix = loginConfig.getLoginExcludeSuffix();

            // 不过滤的 pattern
            String[] excludePattern = loginConfig.getLoginExcludePatterns();

            for (String prefix : excludePrefix) {
                if (uri.startsWith(prefix)) {
                    filterChain.doFilter(srequest, sresponse);
                    return;
                }
            }

            for (String suffix : excludeSuffix) {
                if (uri.endsWith(suffix)) {
                    filterChain.doFilter(srequest, sresponse);
                    return;
                }
            }

            for (String pattern : excludePattern) {
                if (uri.contains(pattern)) {
                    filterChain.doFilter(srequest, sresponse);
                    return;
                }
            }

            // TODO 本地开发不开启校验
//            if (DevelopPermitUtil.access(request)) {
//                filterChain.doFilter(srequest, sresponse);
//                return;
//            }

            boolean loginFlag = false;

            /*// 查询cookie
            Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
            if (cookie == null) {
                log.warn("【登录校验】Cookie中查不到Token");
                throw new AuthorizeException();
            }

            // 去redis里面查询
            String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
            if (StringUtils.isEmpty(tokenValue)) {
                log.warn("【登录校验】Redis中查不到Token");
                throw new AuthorizeException();
            }*/

            String paramToken = request.getParameter(CookieConstant.TOKEN);
            String cookieToken = null;
            Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
            if (cookie != null) {
                cookieToken = cookie.getValue();
            }

            String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;

            if (StringUtils.isEmpty(token)) {
                log.warn("【登录校验】Token不存在");
                throw new AuthorizeException();
            }

            // 去redis里面查询
            String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, token));
            if (StringUtils.isEmpty(tokenValue)) {
                log.warn("【登录校验】Redis中查不到Token");
                // 移动端判断
                if (!StringUtils.isEmpty(paramToken)) {
                    throw new MobileException();
                } else {
                    throw new AuthorizeException();
                }
            } else {
                // 延长 redis 中数值的 ttl, 如为 pc 请求，还需延长 cookie 有效期
                redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), tokenValue, RedisConstant.EXPIRE, TimeUnit.SECONDS);
                // 延长 cookie 有效期
                if (!StringUtils.isEmpty(cookieToken)) {
                    CookieUtil.set(response, CookieConstant.TOKEN, token, CookieConstant.EXPIRE);
                }
            }

            loginFlag = true;

            if (loginFlag) {
                filterChain.doFilter(srequest, sresponse);
                return;
            } else {
                // 没有登录且未在白名单
                throw new AuthorizeException();
            }
        }

        @Override
        public void init(FilterConfig arg0) throws ServletException { }
    }
}