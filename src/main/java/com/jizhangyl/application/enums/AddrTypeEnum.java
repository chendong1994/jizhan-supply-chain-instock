package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/5 13:41
 * @description
 */
@Getter
public enum AddrTypeEnum {
    DEFAULT_ADDRESS(0, "是"),
    OTHER_ADDRESS(1, "否"),
    ;

    private Integer code;

    private String msg;

    AddrTypeEnum() {
    }

    AddrTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
