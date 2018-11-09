package com.jizhangyl.application.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年11月9日 下午1:27:07
 * @description 用于导出现货订单
 */
@Data
public class InstockOrderDTO implements Serializable {

    private static final long serialVersionUID = 3244662488117027280L;

    private String createTime; // 订单时间

    private String jzOrderId; // 集栈订单号编号

    private String janCodes; // 商品jan code

    private String orderStatus; // 订单状态

    private String inviteCode; // 买手邀请码

    private String buyerName; // 买手名字

    private String expCorp; // 物流承运方

    private String expressNumber; // 快递单号

    private String buyerNickName; // 买家昵称

    private String deliveryRange; // 派送范围

    private String reciverProvince; // 收件省

    private String reciverCity; // 收件市

    private String reciverArea; // 收件区

    private String reciverStreet; // 收件街道

    private String reciverName; // 收件姓名

    private String reciverCompany; // 收件公司

    private String reciverMobile; // 收件手机

    private String reciverPhone; // 收件座机

    private String reciverZipCode; // 收件邮编

    private String sellerRemark; // 卖家备注

    private String buyerRemark; // 买家备注

    private String orderId; // 订单编号

    private String printId; // 打印编号

    private String packCodes; // 订单物品（打包码）

    private String defineRemark; // 自定义备注

    private String printNums; // 打印份数

    private String templateName; // 模板名

    private String weight; // 重量

    private String price; // 价格

    private String sellerNickName; // 卖家昵称

    private String senderProvince; // 发件省

    private String senderCity; // 发件市

    private String senderArea; // 发件区

    private String senderStreet; // 发件街道

    private String senderName; // 发件姓名

    private String senderCompany; // 发件公司

    private String senderMobile; // 发件手机

    private String senderPhone; // 发件座机

    private String senderZipCode; // 发件邮编

    private String bigPen; // 大头笔
}
