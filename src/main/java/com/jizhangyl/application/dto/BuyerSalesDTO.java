package com.jizhangyl.application.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年9月17日 下午1:36:14
 * @description 存储买手的销售日期,买手邀请码,买手姓名,买手销售额,订单量,买手下游人数
 */
@Data
public class BuyerSalesDTO {

	private String date; // 日期

	private String inviteCode; // 买手邀请码

	private String buyerName; // 买手姓名

	private BigDecimal buyerSales; // 销售额

	private Integer orderNums; // 订单量

	private Integer downStreamCount; // 买手下游人数

	@Override
	public String toString() {
		return "BuyerSalesDTO [date=" + date + ", inviteCode=" + inviteCode + ", buyerName=" + buyerName
				+ ", buyerSales=" + buyerSales + ", orderNums=" + orderNums + ", downStreamCount=" + downStreamCount
				+ "]";
	}
}
