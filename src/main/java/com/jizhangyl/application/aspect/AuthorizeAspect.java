package com.jizhangyl.application.aspect;

import com.jizhangyl.application.constant.CookieConstant;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.exception.AuthorizeException;
import com.jizhangyl.application.exception.MobileException;
import com.jizhangyl.application.utils.CookieUtil;
import com.jizhangyl.application.utils.DevelopPermitUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨贤达
 * @date 2018/8/3 14:10
 * @description
 */
@Slf4j
@Aspect
@Component
//@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod")
public class AuthorizeAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Pointcut("execution(public * com.jizhangyl.application.controller.*.*(..))" +
    "&& !execution(public * com.jizhangyl.application.controller.WechatUserController.*(..))" +
    "&& !execution(public * com.jizhangyl.application.controller.WechatController.*(..))" +
    "&& !execution(public * com.jizhangyl.application.controller.TestController.*(..))" +
    "&& !execution(public * com.jizhangyl.application.controller.PayController.*(..))")
    public void verify() {}

    @Before("verify()")
    public void doVerify() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        // TODO 本地开发不开启校验
//        if (DevelopPermitUtil.access(request)) {
//            return;
//        }
        String uri = request.getRequestURI();
        log.info("【登录校验】request uri = {}", uri);

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
    }
}