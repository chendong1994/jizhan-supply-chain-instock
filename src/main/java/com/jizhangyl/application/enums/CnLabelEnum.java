package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/10/23 11:11
 * @description
 */
@Getter
public enum CnLabelEnum {
    DEFAULT(0, "没有"),
    EXISTED(1, "有"),
    ;

    private Integer code;

    private String msg;

    CnLabelEnum() {
    }

    CnLabelEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
