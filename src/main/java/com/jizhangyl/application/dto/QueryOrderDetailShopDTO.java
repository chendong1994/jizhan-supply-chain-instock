package com.jizhangyl.application.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年9月25日 下午7:36:16
 * @description 订单管理页面->订单详情窗口->商品信息
 */
@Data
public class QueryOrderDetailShopDTO {

    private String jancode; // 商品jancode
    
    private String img; // 商品img
    
    private String productName; // 商品标题
    
    private BigDecimal cost; // 售价
    
    private BigDecimal taxes; // 关税
    
    private Integer num; // 数量
}
