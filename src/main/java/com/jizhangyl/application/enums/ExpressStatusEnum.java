package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/24 11:26
 * @description
 */
@Getter
public enum ExpressStatusEnum {
    FINISHED(0, "已签收"),
    ;
    private Integer code;

    private String message;

    ExpressStatusEnum() {
    }

    ExpressStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
