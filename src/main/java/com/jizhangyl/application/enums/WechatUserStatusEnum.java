package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/2 19:24
 * @description
 */
@Getter
public enum WechatUserStatusEnum {

    NOT_ACTIVATE(0, "待激活，需填写邀请码"),
    ACTIVATED(1, "已激活"),
    NEW(2, "新增"),
    ;

    private Integer code;

    private String msg;

    WechatUserStatusEnum() {
    }

    WechatUserStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
