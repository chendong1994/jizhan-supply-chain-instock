package com.jizhangyl.application.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年9月17日 下午1:29:37
 * @description 存储商品的id,名称以及对应的销售额
 */
@Data
public class ShopSalesDTO {

	private String date; // 某个时间段

	private String shopJan; // 商品的jancode

	private String shopName; // 商品名称

	private BigDecimal shopSales; // 销售额(货值+关税)

	private Integer shopSalesNum; // 销量

	private Integer inventory; // 库存
}