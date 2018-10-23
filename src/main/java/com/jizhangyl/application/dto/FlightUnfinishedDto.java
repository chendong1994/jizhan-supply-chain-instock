package com.jizhangyl.application.dto;

import lombok.Data;

@Data
public class FlightUnfinishedDto {
	
	private Integer flightId;
	
	//1是完成2是未完成
	
	private Integer packPackageStatus;//到港状态1是打包2是没打包

    private Integer packageToAirportStatus;//1是包裹送机2是没包裹送机

    private Integer flightChargeStatus;//1是收航空费2是没收航空费

    private Integer flightArrivalStatus;//1是航班到港2是没航班到港

    private Integer customsClearanceStatus;//1是清关完毕2是没清关完毕

}
