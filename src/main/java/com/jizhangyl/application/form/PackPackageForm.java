package com.jizhangyl.application.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PackPackageForm {
	
	@NotBlank(message="物流单号为空")
	private String expressNums;//物流单号集合
	
	@NotNull(message="航班流程主键为空")
	private Integer flightId;
	
	
	
	
	
	

}
