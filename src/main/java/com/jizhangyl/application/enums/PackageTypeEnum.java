package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:53
 * @description
 */
@Getter
public enum PackageTypeEnum {
    BC(0, "BC件"),
    CC(1, "CC件"),
    JP(2, "日本邮政件"),
    ;

    private Integer code;

    private String packageType;

    PackageTypeEnum() {
    }

    PackageTypeEnum(Integer code, String packageType) {
        this.code = code;
        this.packageType = packageType;
    }
}
