package com.jizhangyl.application.utils;

import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨贤达
 * @date 2018/9/13 11:05
 * @description
 */
@Slf4j
public class OrderExcelFieldCheckUtil {

    public static <T> Integer fieldToInt(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        try {
            if (clazz == String.class) {
                return Integer.parseInt((String) value);
            } else if (clazz == int.class || clazz == Integer.class) {
                return (Integer) value;
            } else if (clazz == double.class || clazz == Double.class) {
                return ((Double) value).intValue();
            } else {
                return (Integer) value;
            }
        } catch (Exception e) {
            log.error("【类型转换】错误, value = {}", value);
            throw new GlobalException(ResultEnum.TYPE_CONVERT_ERROR);
        }
    }

    public static boolean checkValueRound(Integer value) {
        if (value >= 1 && value <= 25) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {

        String s = "1";
        int i = 1;
        double d = 1.01;
        Object o = 1;
        System.out.println(fieldToInt(s));
        System.out.println(fieldToInt(i));
        System.out.println(fieldToInt(d));
        System.out.println(fieldToInt(o));
    }
}
