package com.jizhangyl.application.form;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OaBillForm {
	
	private Integer oaBillId;
	
    @NotNull(message="支出收入类别不能为空")
    private Integer billType;
    
    @NotNull(message="金额别不能为空")
    private BigDecimal billAmount;

    private String remark;
	

}
