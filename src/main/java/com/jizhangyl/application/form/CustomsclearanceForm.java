package com.jizhangyl.application.form;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

/**
 * 
 * @author 陈栋
 * @date 2018年9月26日  
 * @description
 */
@Data
public class CustomsclearanceForm {

	@NotNull(message="主键不能为空")
	private Integer flightId;

//	@NotBlank(message="清关凭证不能为空")
//	private String customsUrl;//清关凭证
	@NotEmpty(message="图片不能为空")
	private List<String> customsUrl;

	@NotNull(message="清关计费重量不能为空")
	private BigDecimal customsWeight;// 清关计费重量

	@NotNull(message="清关计费票数不能为空")
	private Integer customsPoll;// 清关计费票数

	@NotNull(message="清关地面服务费(人民币)不能为空")
	private BigDecimal customsGroundCost;// 清关地面服务费(人民币)

	@NotNull(message="清关费(人民币)不能为空")
	private BigDecimal customsCost;// 清关费(人民币)

	@NotNull(message="清关交易手续费(人民币)不能为空")
	private BigDecimal customsTransactionCost;// 清关交易手续费(人民币)

	@NotNull(message="清关跨境电商税（人民币）不能为空")
	private BigDecimal customsBorderCost;// 清关跨境电商税（人民币）

	@NotNull(message="清关EMS国内派送费（人民币）不能为空")
	private BigDecimal customsEmsCost;// 清关EMS国内派送费（人民币）

	@NotNull(message="清关舱单处理费（人民币）不能为空")
	private BigDecimal customsShippingCost;// 清关舱单处理费（人民币）

	private String customsOther;// 清关其他费用标题

	private BigDecimal customsOtherCost;// 清关其他费用
	
	

}
