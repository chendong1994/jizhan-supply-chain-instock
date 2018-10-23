package com.jizhangyl.application.dataobject;

import java.util.Date;

import lombok.Data;

/**
 * @author 杨贤达
 * @date 2018/8/22 20:33
 * @description 导出至海关的 excel 的行对象封装
 */
@Data
public class OrderForCustoms {

    /**
     * 收件人名称
     */
    private String recvName;

    /**
     * 购买人证件号
     */
    private String buyerIdNum;

    /**
     * 收件人电话
     */
    private String recvMobile;

    /**
     * 收货人地址
     */
    private String recvAddr;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * SKU(电商平台具体商品编号)
     * 我们系统中叫 海关商品唯一码
     */
    private String customsProductId;

    /**
     * 商品数量
     */
    private String productQuantity;

    /**
     * 运单号
     */
    private String expressNumber;


    /**
     * 包裹毛重
     */
    private String netWeight;

    /**
     * 商品jan code
     */
    private String productJancode;

    /**
     * 海关商品分类编号
     */
    private String customsCateType;

    /**
     * 海关税则号列
     */
    private String customsTariffLine;

    /**
     * 商品简写识别码
     */
    private String productSimpleCode;

    /**
     * 报关售价总和
     */
    private String totalBcPrice;

    /**
     * 报关税金总和
     */
    private String totalTaxes;

    /**
     * 打包重量总和
     */
    private String totalPackWeight;

    /**
     * 供货价总和
     */
    private String totalGPrice;


    /**
     * 商品关税总和
     */
    private String totalDuties;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 包裹净重
     */
    private String jWeight;

    /**
     * 买手名字
     */
    private String buyerName;

    /**
     * 代购手机号码
     */
    private String buyerMobile;

    /**
     * 代购邀请码
     */
    private String inviteCode;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 订单时间
     */
    private String orderTime;
    
    
    /**
     * 提运单号
     */
    private String deliveryNumber;

    /**
     * 航次
     */
    private String voyage;

    /**
     * 预计到达时间
     */
    private Date expectedArrivalTime;
    
    
    
}
