package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/3 11:53
 * @description
 */
@Getter
public enum InviteCodeStatusEnum {
    INVITE_CODE_RIGHT(1, "邀请码正确"),
    INVITE_CODE_ERROR(0, "邀请码错误"),
    ;

    private Integer code;

    private String msg;

    InviteCodeStatusEnum() {
    }

    InviteCodeStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
