package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/12 10:55
 * @description
 */
@Getter
public enum PayTypeEnum {
    WX_PAY(0, "微信支付"),
    ;
    private Integer code;

    private String msg;

    PayTypeEnum() {
    }

    PayTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
