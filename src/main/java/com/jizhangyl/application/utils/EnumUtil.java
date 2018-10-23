package com.jizhangyl.application.utils;

import com.jizhangyl.application.enums.CodeEnum;

/**
 * @author 杨贤达
 * @date 2018/8/20 14:21
 * @description
 */
public class EnumUtil {

    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {
        for (T each : enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }
}
