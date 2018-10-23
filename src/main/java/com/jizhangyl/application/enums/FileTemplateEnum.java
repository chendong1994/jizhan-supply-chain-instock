package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/9/5 18:05
 * @description
 */
@Getter
public enum FileTemplateEnum {
    BUYER(1, "代购订单导入模板"),
    DELIVERY(2, "仓库发货模板"),
    ;

    private Integer no;

    private String name;

    FileTemplateEnum() {
    }

    FileTemplateEnum(Integer no, String name) {
        this.no = no;
        this.name = name;
    }
}
