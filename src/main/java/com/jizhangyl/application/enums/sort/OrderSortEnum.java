package com.jizhangyl.application.enums.sort;

import lombok.Getter;

/**
 * @author 曲健磊
 * @date 2018年10月20日 下午4:55:31
 * @description 排序的顺序
 */
@Getter
public enum OrderSortEnum {
    ASC(1, "升序"), // 由低->高
    DESC(2, "降序"), // 由高-低
    ;

    private Integer code;

    private String message;

    OrderSortEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
