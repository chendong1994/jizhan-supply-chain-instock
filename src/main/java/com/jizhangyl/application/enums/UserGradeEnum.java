package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/8/5 13:41
 * @description
 */
@Getter
public enum UserGradeEnum {
    ZERO(0),
    ;

    private Integer grade;

    UserGradeEnum() {
    }

    UserGradeEnum(Integer grade) {
        this.grade = grade;
    }
}
