package com.jizhangyl.application.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.PayStatusEnum;
import com.jizhangyl.application.utils.EnumUtil;
import com.jizhangyl.application.utils.serializer.CustomDateSerializer;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/7/26 17:49
 * @description 订单主表实体类
 */
@Data
@Entity
@DynamicUpdate
public class OrderMaster {

    /**
     * 订单编号
     */
    @Id
    private String orderId;

    /**
     * 订单总金额
     */
    private BigDecimal orderAmount;

    /**
     * 订单实收货值
     */
    private BigDecimal orderCost;

    /**
     * 订单实收税金
     */
    private BigDecimal orderTaxes;

    /**
     * 订单实收运费
     */
    private BigDecimal orderFreight;

    /**
     * 用户名称
     */
    private String buyerName;


    /**
     * 用户手机
     */
    private String buyerPhone;

    /**
     * 发件人地址id
     */
    private Integer buyerAddrId;

    /**
     * 代购openid
     */
    private String buyerOpenid;

    /**
     * 收件人名称
     */
    private String recipient;

    /**
     * 收件人证件号
     */
    private String recipientIdCard;

    /**
     * 收件人手机号码
     */
    private String recipientPhone;

    /**
     * 收件人地址id
     */
    private Integer recipientAddrId;

    /**
     * 运单号
     */
    private String expressNumber;

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

    /**
     * 订单状态, 默认为新下单
     */
    private Integer orderStatus = OrderStatusEnum.NEW.getCode();

    /**
     * 支付状态, 默认未支付
     */
    private Integer payStatus = PayStatusEnum.WAIT.getCode();

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    /**
     * 订单商品净重
     */
    private BigDecimal netWeight;

    /**
     * 订单打包重量
     */
    private BigDecimal packWeight;

    /**
     * 订单商品总体积
     */
    private BigDecimal totalVolume;

    /**
     * 所需纸箱尺寸
     */
    private Integer boxSize;


    /**
     * 买家导入订单时候指定的订单编号
     */
    @Column(name = "ext_1")
    private String buyerOrderId;

    /**
     * 买家导入订单时候买家会员名
     */
    @Column(name = "ext_2")
    private String customerName;

    /**
     * 付款时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date payTime;

    /**
     * 发货日期
     */
    private Date deliveryTime;

    /**
     * 上一次提醒上传证件时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date lastNotifyTime;

    /**
     * 货件类型
     */
    private Integer packageType;

    @JsonIgnore
    public OrderStatusEnum getOrderStatusEnum() {
        return EnumUtil.getByCode(orderStatus, OrderStatusEnum.class);
    }
}
