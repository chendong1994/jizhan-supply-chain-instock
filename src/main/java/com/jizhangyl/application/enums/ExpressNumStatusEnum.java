package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/2 10:57
 * @description
 */
@Getter
public enum ExpressNumStatusEnum {
    UNUSED(0, "未使用"),
    USED(1, "已使用"),
    ;

    private Integer code;

    private String msg;

    ExpressNumStatusEnum() {
    }

    ExpressNumStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
