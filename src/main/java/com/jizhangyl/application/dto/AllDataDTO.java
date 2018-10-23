package com.jizhangyl.application.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年9月17日 上午10:16:04
 * @description 存储某一天的全站数据(货值,关税,货值+关税,运费,订单数,客单价)
 */
@Data
public class AllDataDTO {

	/**
	 * 哪一天的全站数据
	 */
	private String date;

	/**
	 * 货值
	 */
	private BigDecimal cost;

	/**
	 * 关税
	 */
	private BigDecimal taxes;

	/**
	 * 货值+关税
	 */
	private BigDecimal costAndTaxes;

	/**
	 * 运费
	 */
	private BigDecimal freight;

	/**
	 * 订单数
	 */
	private Integer orderNums;

	/**
	 * 客单价
	 */
	private BigDecimal customerPrice;

	@Override
	public String toString() {
		return "AllDataDTO [date=" + date + ", cost=" + cost + ", taxes=" + taxes + ", costAndTaxes=" + costAndTaxes
				+ ", freight=" + freight + ", orderNums=" + orderNums + ", customerPrice=" + customerPrice + "]";
	}
}
