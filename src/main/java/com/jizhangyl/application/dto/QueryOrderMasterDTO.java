package com.jizhangyl.application.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年9月25日 下午7:22:00
 * @description 订单管理页面中的订单实体类
 */
@Data
public class QueryOrderMasterDTO {

    private String createTime; // 订单时间

    private String status; // 订单状态

    private String expressNum; // 物流单号

//    private String expressStatus; // 最近的一条物流状态
    
    private String deliveryNum; // 航班提运单号

    private String inviteCode; // 买手邀请码

    private String buyerName; // 买手姓名

    private String senderName; // 发件人名称
    
    private String senderPhone; // 发件人电话
    
    private BigDecimal costAndTaxes; // 货值+关税

    private BigDecimal freight; // 运费

    private String orderId; // 订单id,用于查询订单明细

    private String recipient; // 收件人
    
    private String recipientPhone; // 收件人电话号码
    
    private String deliveryTime; // 打包时间
}
