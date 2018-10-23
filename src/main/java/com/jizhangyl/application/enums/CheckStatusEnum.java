package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/5 13:41
 * @description
 */
@Getter
public enum CheckStatusEnum {
    WAIT_CONFIRM(0, "待审核"),
    PASSED(1, "已通过"),
    FAIL(2, "未通过"),
//    UPLOADED(3, "已上传"),
    ;

    private Integer code;

    private String msg;

    CheckStatusEnum() {
    }

    CheckStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
