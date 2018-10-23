package com.jizhangyl.application.form;

import javax.validation.constraints.NotNull;

import lombok.Data;


@Data
public class FlightArrivalForm {
	
	 @NotNull(message="主键不能为空")
	 private Integer flightId;
	 
	 
	
	

}
