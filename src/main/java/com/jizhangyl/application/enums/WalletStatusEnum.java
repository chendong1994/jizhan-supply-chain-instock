package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * 钱包激活状态
 * @author 陈栋
 * @date 2018年10月16日  
 * @description
 */
@Getter
public enum WalletStatusEnum {
	
	
	    /**激活**/
	    OPEN(0, "激活"),
	    /**冻结**/
	    CLOSE(1, "冻结"),
	    
	    ;

	    private Integer code;

	    private String msg;

	    WalletStatusEnum() {
	    }

	    WalletStatusEnum(Integer code, String msg) {
	        this.code = code;
	        this.msg = msg;
	    }
	
	

}
