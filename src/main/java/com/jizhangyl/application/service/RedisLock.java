package com.jizhangyl.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 杨贤达
 * @date 2018/9/17 15:29
 * @description
 */
@Component
@Slf4j
public class RedisLock {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 加锁
     * @param key
     * @param value 当前时间 + 超时时间
     * @return
     */
    public boolean lock(Object key, String value) {
        if(redisTemplate.opsForValue().setIfAbsent(key, value)) {
            return true;
        }
        String currentValue = String.valueOf(redisTemplate.opsForValue().get(key));
        if (!StringUtils.isEmpty(currentValue)
                && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            String oldValue = String.valueOf(redisTemplate.opsForValue().getAndSet(key, value));
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解锁
     * @param key
     * @param value
     */
    public void unlock(Object key, String value) {
        try {
            String currentValue = String.valueOf(redisTemplate.opsForValue().get(key));
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch (Exception e) {
            log.error("【redis分布式锁】解锁异常, {}", e);
        }
    }

}