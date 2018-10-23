package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/7/26 18:12
 * @description
 */
@Getter
public enum  OrderStatusEnum implements CodeEnum {
    NEW(0, "下单待付款"),
    PAID(1, "已付款待发货"),
    DELIVERED(2, "已发货"),
    RECEIVED(3, "已收货"),
    CANCELED(4, "已取消"),

    /**
     * 仅用于查询所有订单
     */
    FIND_ALL(5, "查询所有"),
    ;

    private Integer code;

    private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
