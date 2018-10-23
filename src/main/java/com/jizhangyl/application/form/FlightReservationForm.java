package com.jizhangyl.application.form;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class FlightReservationForm {
	
	 private Integer flightId;
	
	 @NotBlank(message="提运单号为空")
	 private String deliveryNumber;//提运单号
	 
	 @NotBlank(message="航次为空")
	 private String voyage;//航次
	 
	 private String flightGoTime;//航班起飞时间
	 
	 @NotBlank(message="航班预计到达时间为空")
	 private String flightArriveTime;//航班预计到达时间
	 
	 
	 

}
