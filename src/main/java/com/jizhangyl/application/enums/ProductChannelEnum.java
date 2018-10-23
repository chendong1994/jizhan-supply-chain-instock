package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/10/23 11:16
 * @description
 */
@Getter
public enum ProductChannelEnum {
    DEFAULT(0, "XXX"),
    GENERAL_TRADE(1, "一般贸易");

    private Integer code;

    private String msg;

    ProductChannelEnum() {
    }

    ProductChannelEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
