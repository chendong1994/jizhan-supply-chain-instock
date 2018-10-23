package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 曲健磊
 * @date 2018年9月23日 下午3:46:57
 * @description shiro所需的相关常量
 */
@Getter
public enum ShiroEnum {
    OPEN_PWD("JIZHANGYL", "openid默认密码"),
    ;

    private String code;

    private String msg;

    ShiroEnum() {
    }

    ShiroEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
