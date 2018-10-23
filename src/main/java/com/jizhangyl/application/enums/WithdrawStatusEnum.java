package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * 提现状态
 * @author 陈栋
 * @date 2018年10月17日  
 * @description
 */
@Getter
public enum WithdrawStatusEnum {
	
	
	/**提现成功**/
	SUCCEED(0, "提现成功"),
    /**提现失败**/
    FAIL(1, "提现失败"),
    
    ;

    private Integer code;

    private String msg;

    WithdrawStatusEnum() {
    }

    WithdrawStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
