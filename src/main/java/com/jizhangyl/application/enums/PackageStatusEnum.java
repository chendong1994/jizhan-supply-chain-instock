package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * 包裹状态
 * @author 陈栋
 * @date 2018年9月26日  
 * @description
 */
@Getter
public enum PackageStatusEnum {
	
	/**
	 *航班预定 
	 */
	FLIGHT_RESERVATION(1,"航班预定"),
	/**
	 * 包裹打包
	 */
	PACK_PACKAGE(2,"包裹打包"),
	/**
	 * 包裹送机
	 */
	PACKAGE_TO_AIRPORT(3,"包裹送机"),
	/**
	 * 收航空费
	 */
	FLIGHT_CHARGE(4,"收航空费"),
	/**
	 * 航班到港
	 */
	FLIGHT_ARRIVAL(5,"航班到港"),
	/**
	 * 清关完毕
	 */
	CUSTOMS_CLEARANCE(6,"清关完毕")
	
	; 
	
	
	
	private Integer code;

    private String msg;

    PackageStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public static String getMsg(int code) {
		for (PackageStatusEnum type : PackageStatusEnum.values()) {
			if (type.getCode() == code) {
				return type.getMsg();
			}
		}
		return null;
	}
    
    
    

}
