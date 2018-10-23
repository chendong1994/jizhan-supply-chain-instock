package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/9 17:42
 * @description
 */
@Getter
public enum CateStatusEnum {
    UP(0, "正常"),
    DOWN(1, "失效"),
    ;

    private Integer code;

    private String message;

    CateStatusEnum() {
    }

    CateStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
