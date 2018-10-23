package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * 用户权益等级
 * @author 陈栋
 * @date 2018年9月18日  
 * @description
 */
@Getter
public enum UserGardeEnum {
	
	ZERO(0,"新人"),
	ONE(1, "1级"),
	TWO(2, "2级"),
	THREE(3, "3级"),
	FOUR(4, "4级"),
	FIVE(5, "5级"),
	SIX(6, "6级"),
	SEVEN(7, "7级"),
	EIGHT(8, "8级"),
	NINE(9, "9级"),
	TEN(10, "10级"),
	ELEVEN(11, "11级"),
    
    ;
	
	private Integer code;

    private String msg;

    UserGardeEnum() {
    }

    UserGardeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
	

}
