package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * 
 * @author 陈栋
 * @date 2018年9月29日  
 * @description
 */
@Getter
public enum FlightImageTypeEnum {
	
	/**
	 * 送机费用凭证
	 */
	DELIVERY_FLIGHT_URL(1, "送机费用凭证"),
	/**
	 * 航空提货单
	 */
	FLIGHT_URL(2,"航空提货单"),
	/**
	 * 清关费用凭证
	 */
	CUSTOMS_URL(3,"清关费用凭证"),
	
    ;
	
	private Integer code;

    private String name;

    FlightImageTypeEnum() {
    }

    FlightImageTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
