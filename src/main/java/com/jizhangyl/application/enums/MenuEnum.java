package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午8:32:18
 * @description 菜单的几种类型
 */
@Getter
public enum MenuEnum {
    TOTAL_MENU(0, "总菜单"),
    CATALOG(1, "目录"),
    MENU(2, "菜单"),
    BUTTON(3, "按钮"),
    
    TOP_NUM(1, "顶级菜单的id"),
    ;

    private Integer code;

    private String message;

    MenuEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public MenuEnum fillArgs(Object... args) {
        this.message = String.format(this.message, args);
        return this;
    }
}
