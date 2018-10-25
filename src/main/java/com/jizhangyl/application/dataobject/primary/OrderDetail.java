package com.jizhangyl.application.dataobject.primary;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.utils.serializer.CustomDateSerializer;
import com.jizhangyl.application.utils.serializer.CustomPriceSerializer;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/7/26 17:49
 * @description 订单详情表实体
 */
@Entity
@Data
@DynamicUpdate
public class OrderDetail {

    /**
     * 订单详情编号
     */
    @Id
    private String detailId;

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 商品编号
     */
    private String productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 当前价格,单位分
     */
    private BigDecimal productPrice;

    /**
     * 数量
     */
    private Integer productQuantity;

    /**
     * 小图
     */
    private String productIcon;

    /**
     * 订单详情 -> 税金
     */
    @JsonSerialize(using = CustomPriceSerializer.class)
    private BigDecimal detailTaxes;


    /**
     * 订单详情运费
     */
    private BigDecimal detailFreight;

    /**
     * 订单详情货值
     */
    private BigDecimal detailCost;

    /**
     * 订单详情总价
     */
    private BigDecimal detailAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
