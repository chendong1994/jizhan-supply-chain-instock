package com.jizhangyl.application.enums;

import lombok.Getter;

@Getter
public enum ExpensewWhiteStatusEnum {
	
	/**关闭白名单**/
	CLOSE(0, "是"),
	
	/**开启白名单**/
    OPEN(1, "否"),
    
    ;

    private Integer code;

    private String msg;

    ExpensewWhiteStatusEnum() {
    }

    ExpensewWhiteStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
