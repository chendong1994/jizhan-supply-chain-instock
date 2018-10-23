package com.jizhangyl.application.constant;

/**
 * @author 杨贤达
 * @date 2018/7/30 10:35
 * @description
 */
public interface RedisConstant {

    String TOKEN_PREFIX = "token_%s";

    Integer EXPIRE = 7200; //2小时

    /**
     * 超时时间: 10s
     */
    Integer TIMEOUT = 10 * 1000;
}
