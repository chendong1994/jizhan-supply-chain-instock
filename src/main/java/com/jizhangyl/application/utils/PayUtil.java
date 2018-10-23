package com.jizhangyl.application.utils;

import java.util.UUID;

/**
 * @author 杨贤达
 * @date 2018/8/12 11:14
 * @description
 */
public class PayUtil {

    /**
     * 时间戳（秒）
     * 从 1970.1.1 00:00:00 到至今的秒数
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 返回 32 位的唯一字符串
     * @return
     */
    public static String getNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
