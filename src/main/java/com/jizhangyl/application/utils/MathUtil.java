package com.jizhangyl.application.utils;

/**
 * @author 杨贤达
 * @date 2018/8/12 16:28
 * @description
 */
public class MathUtil {

    private static final Double MONEY_RANGE = 0.01;

    /**
     * 比较两个金额是否相等
     * @param v1
     * @param v2
     * @return
     */
    public static Boolean equals(Double v1, Double v2) {
        Double result = Math.abs(v1 - v2);
        if (result < MONEY_RANGE) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean equals(Integer v1, Integer v2) {
        return v1.equals(v2);
    }
}
