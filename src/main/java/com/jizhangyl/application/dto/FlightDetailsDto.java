package com.jizhangyl.application.dto;

import java.math.BigDecimal;

import lombok.Data;



@Data
public class FlightDetailsDto {
	
	    private Integer flightId;//主键

	    private Integer packageStatus;//包裹状态

	    private String deliveryNumber;//提运单号

	    private String voyage;//航次

	    private String createTime;//创建时间

	    private String flightArriveTime;//航班预计到达时间

	    private Integer packageNumber;//包裹数量

	    private BigDecimal deliveryFlightCost;//送机费用

	    private String deliveryFlightOther;//送机其他共计费用标题

	    private BigDecimal deliveryFlightOtherCost;//送机其他费用
	    
	    private BigDecimal flightVolume;//航班实收体积

	    private BigDecimal flightWeight;//航班实收重量

	    private BigDecimal flightCost;//航班实收费用(日元）

	    private String flightOthe;//航班其他费用标题

	    private BigDecimal flightOtherCost;//航班其他费用
	    
	    private BigDecimal customsWeight;//清关计费重量

	    private Integer customsPoll;//清关计费票数

	    private BigDecimal customsGroundCost;//清关地面服务费(人民币)

	    private BigDecimal customsCost;//清关费(人民币)

	    private BigDecimal customsTransactionCost;//清关交易手续费(人民币)

	    private BigDecimal customsBorderCost;//清关跨境电商税（人民币）

	    private BigDecimal customsEmsCost;//清关EMS国内派送费（人民币）

	    private BigDecimal customsShippingCost;//清关舱单处理费（人民币）

	    private String customsOther;//清关其他费用标题

	    private BigDecimal customsOtherCost;//清关其他费用

	    private BigDecimal proportion;//吃抛率
	    
	    
	    private Integer status;//1是未完成2是完成
	    
	    private Integer packPackageStatus;//到港状态1是打包2是没打包

	    private Integer packageToAirportStatus;//1是包裹送机2是没包裹送机

	    private Integer flightChargeStatus;//1是收航空费2是没收航空费

	    private Integer flightArrivalStatus;//1是航班到港2是没航班到港

	    private Integer customsClearanceStatus;//1是清关完毕2是没清关完毕
	    
	    private BigDecimal packAllWeight;//用户付款重量

}
