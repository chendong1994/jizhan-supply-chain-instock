package com.jizhangyl.application.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class FlightDto {
	
	private Integer flightId;

	private Integer packageStatus;// 包裹状态

	private String deliveryNumber;// 提运单号

	private String voyage;// 航次

	private String flightGoTime;// 航班起飞时间

	private String flightArriveTime;// 航班预计到达时间

	private Integer packageNumber;// 包裹数量

	private BigDecimal flightWeight;// 航班实收重量

	private BigDecimal proportion;// 吃抛率
	
	/**
     * 订单实收运费
     */
    private BigDecimal orderFreight;
    
    /**
     * 订单打包重量
     */
    private BigDecimal packAllWeight;//用户付款重量
    
    private String createTime;
    
    private Integer status;//1是未完成2是完成
    
    private Integer packPackageStatus;//到港状态1是打包2是没打包

    private Integer packageToAirportStatus;//1是包裹送机2是没包裹送机

    private Integer flightChargeStatus;//1是收航空费2是没收航空费

    private Integer flightArrivalStatus;//1是航班到港2是没航班到港

    private Integer customsClearanceStatus;//1是清关完毕2是没清关完毕

}
