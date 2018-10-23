package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 曲健磊
 * @date 2018年10月10日 下午3:09:13
 * @description 封装日期状态(上午,中午,下午,傍晚,晚上,凌晨,清晨)
 */
@Getter
public enum DateEnum {
    UNKNOW(0, "未知"),
    NEAR_MONRING(1, "早晨"),
    MORNING(2, "上午"),
    NOON(3, "中午"),
    AFTERNOON(4, "下午"),
    EVENING(5, "傍晚"),
    NIGHT(6, "晚上"),
    DEEP_NIGHT(7, "深夜"),
    BEFORE_DOWN(8, "凌晨"),
    ;

    private Integer code;

    private String msg;

    DateEnum() {
    }

    DateEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
