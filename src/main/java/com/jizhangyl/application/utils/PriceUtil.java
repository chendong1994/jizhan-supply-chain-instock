package com.jizhangyl.application.utils;

import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/8/12 11:01
 * @description
 */
public class PriceUtil {
    public static Integer yuanToFee(BigDecimal orderAmount) {
        return orderAmount.multiply(new BigDecimal(100)).intValue();
    }
}
