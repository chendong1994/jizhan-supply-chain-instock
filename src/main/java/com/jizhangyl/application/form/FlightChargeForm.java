package com.jizhangyl.application.form;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * 收航空费
 * @author 陈栋
 * @date 2018年9月26日  
 * @description
 */
@Data 
public class FlightChargeForm {
	
	@NotNull(message="主键id为空")
	private Integer flightId;
	
//	@NotBlank(message="航空提货单为空")
//	private String flightUrl;//上传航空提货单
	@NotEmpty(message="图片不能为空")
	private List<String> flightUrl;

	@NotNull(message="航班实收体积为空")
    private BigDecimal flightVolume;//航班实收体积

	@NotNull(message="航班实收重量为空")
    private BigDecimal flightWeight;//航班实收重量

	@NotNull(message="航班实收费用(日元）为空")
    private BigDecimal flightCost;//航班实收费用(日元）

    private String flightOthe;//航班其他费用标题

    private BigDecimal flightOtherCost;//航班其他费用

}
