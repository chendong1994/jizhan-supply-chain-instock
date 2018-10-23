package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/15 16:03
 * @description 1-员工, 2-代购
 */
@Getter
public enum RoleTypeEnum {
    ADMIN(1, "员工"),
    BUYER(2, "代购"),
    ;

    private Integer code;

    private String msg;

    RoleTypeEnum() {
    }

    RoleTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
