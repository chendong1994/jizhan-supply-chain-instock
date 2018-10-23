package com.jizhangyl.application.service;

import com.jizhangyl.application.MainApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 杨贤达
 * @date 2018/10/16 17:02
 * @description
 */
public class RedisLockTest extends MainApplicationTests {

    private static int counter = 0;

    @Autowired
    private RedisLock redisLock;

    private void printCounter(long threadId) {

        long time = System.currentTimeMillis() + 10000;

        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            System.out.println("加锁失败。。。");
        }

        System.out.println(threadId + "====" + ++counter);

        redisLock.unlock(this.getClass(), String.valueOf(time));
    }

    @Test
    public void lockTest() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    printCounter(Thread.currentThread().getId());
                }
            }).start();
        }

    }
}