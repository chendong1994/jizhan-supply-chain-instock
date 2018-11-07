package com.jizhangyl.application.form;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PackageToAirportForm {
	
	@NotNull(message="航班流程主键为空")
	private Integer flightId;
	
//	@NotBlank(message="上传送机费用凭证不能为空")
//	private String deliveryFlightUrl;
	@NotEmpty(message="图片不能为空")
	private List<String> deliveryFlightUrl; 
	
	@NotNull(message="送机费用不能为空")
	private BigDecimal deliveryFlightCost;//送机费用
	
    private String deliveryFlightOther;//送机其他共计费用标题
    
    private BigDecimal deliveryFlightOtherCost;//送机其他费用 
    

}
