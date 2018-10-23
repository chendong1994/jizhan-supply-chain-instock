package com.jizhangyl.application.aspect;

import com.jizhangyl.application.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author 杨贤达
 * @date 2018/8/3 14:12
 * @description 获取 Http 请求的 request, response 中相关信息
 */
@Slf4j
@Aspect
@Component
public class HttpAspect {

    @Pointcut("execution(public * com.jizhangyl.application.controller.*.*(..))")
    public void log() {}

    /**
     * 打印请求参数
     * @param joinPoint
     */
    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        StringBuffer sb = new StringBuffer();
        sb.append("{\n");
        sb.append("\turl: ").append(request.getRequestURL()).append("\n");
        sb.append("\tmethod: ").append(request.getMethod()).append("\n");
        sb.append("\tip: ").append(request.getRemoteAddr()).append("\n");
        sb.append("\tclass_method: ").append(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()).append("\n");
        sb.append("\targs: ").append(Arrays.toString(joinPoint.getArgs())).append("\n");
        sb.append("}");

        log.info("\nrequest: \n{}", sb);

    }

    /**
     * 打印响应内容
     * @param object
     */
    @AfterReturning(returning = "object", pointcut = "log()")
    public void doAfterReturning(Object object) {
        if (object instanceof ResponseEntity) {
            log.info("【文件•下载/导出】正在解析中......");
        } else {
            log.info("\nresponse: \n{}", JsonUtil.toJson(object));
        }
    }
}
