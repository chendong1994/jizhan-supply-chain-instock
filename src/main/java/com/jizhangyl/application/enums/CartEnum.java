package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/2 10:57
 * @description
 */
@Getter
public enum CartEnum {
    PRODUCT_QUANTITY_UNIT(1, "数量变化单元"),
    ;

    private Integer code;

    private String msg;

    CartEnum() {
    }

    CartEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
