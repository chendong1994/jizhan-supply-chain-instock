package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 曲健磊
 * @date 2018年10月25日 下午8:00:44
 * @description 记录系统用户的一些状态
 */
@Getter
public enum UserInfoEnum {
    FREE(0, "未冻结"),
    FREEZED(1, "已经冻结"),
    ;
    
    private Integer code;

    private String msg;

    UserInfoEnum() {
    }

    UserInfoEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
