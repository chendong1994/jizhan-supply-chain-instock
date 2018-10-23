package com.jizhangyl.application.enums;

import com.jizhangyl.application.utils.EnumUtil;
import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/16 12:59
 * @description
 */
@Getter
public enum ShopStatusEnum implements CodeEnum {

    UP(0, "上架"),
    DOWN(1, "下架"),
    ;

    private Integer code;

    private String msg;

    ShopStatusEnum() {
    }

    ShopStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
