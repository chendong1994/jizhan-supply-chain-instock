package com.jizhangyl.application.utils;

/**
 * @author 杨贤达
 * @date 2018/8/28 17:16
 * @description
 */
public class RoundUtil {

    public static double computeRound(double val) {
        if (val - 1.0 < 0.0001) {
            return 1;
        }
        double result = val - (int) val;
        if (result == 0) {
            return val;
        } else if (result > 0.5) {
            return (int) val + 1;
        } else {
            return (int) val + 0.5;
        }
    }
}
