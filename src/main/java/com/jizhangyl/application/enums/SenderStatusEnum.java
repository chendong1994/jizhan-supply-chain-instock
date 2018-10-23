package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/8 14:26
 * @description
 */
@Getter
public enum SenderStatusEnum {
    NOT_DEFAULT(0, "非默认发件人"),
    DEFAULT(1, "默认发件人"),
    ;

    private Integer code;

    private String message;

    SenderStatusEnum() {
    }

    SenderStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
