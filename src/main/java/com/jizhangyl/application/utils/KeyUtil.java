package com.jizhangyl.application.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author 杨贤达
 * @date 2018/8/3 11:34
 * @description
 */
public class KeyUtil {

    private static final char[] CHARARRAY = {
            'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'J', 'K', 'L',
            'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z'
    };

    /**
     * 生成唯一的主键
     * 格式: 时间+随机数
     * @return
     */
    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date()) + String.valueOf(number)  ;
    }

    /**
     * 生成邀请码
     * @return
     */
    public static String genKey() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;
        int index = random.nextInt(24);
        return String.valueOf(CHARARRAY[index]) + number;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            System.out.println(KeyUtil.genUniqueKey());
        }
    }
}
