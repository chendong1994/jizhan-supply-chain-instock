package com.jizhangyl.application.dataobject.primary;

import lombok.Data;

/**
 * @author 杨贤达
 * @date 2018/8/22 20:33
 * @description 导出至仓库的 excel 的行对象封装
 */
@Data
public class OrderForRepository {

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
     * 运单号
     */
    private String expressNumber;

    /**
     * 包裹毛重
     */
    private String netWeight;

    /**
     * 商品jan code, 此处为多个拼接而成的 jancode 串
     * 496496479649/46363634643634/4634643634
     */
    private String productJancode;


    /**
     * 集栈订单号编号
     */
    private String orderId;

    /**
     * 商品简写识别码，此处为多个拼接而成
     * 如：DA535*2/AR325*3/XBD35*4
     * 商品简写码乘以数量，以斜杠分割
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
     * 订单实收打包重量
     */
    private String actualPackWeight;

    /**
     * 订单实收运费
     */
    private String actualFreight;

    /**
     * 订单状态
     */
    private String orderStatus;

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
     * 订单时间
     */
    private String orderTime;
}
