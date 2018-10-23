package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/5 13:41
 * @description
 */
@Getter
public enum CertSaveStatusEnum {
    NOT_SAVE(0, "不保存"),
    SAVE(1, "保存"),
    ;

    private Integer code;

    private String msg;

    CertSaveStatusEnum() {
    }

    CertSaveStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
