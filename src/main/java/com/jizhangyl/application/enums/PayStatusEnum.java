package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/7/26 18:14
 * @description
 */
@Getter
public enum PayStatusEnum {
    WAIT(0, "等待支付"),
    SUCCESS(1, "已支付"),

    ;

    private Integer code;

    private String msg;

    PayStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
