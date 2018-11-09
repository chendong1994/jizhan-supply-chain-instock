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
    PAID(1, "待发货"),
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
    
    // 将状态码转换成对应的字符串
    public static String transStr(Integer status) {
        for (OrderStatusEnum enums : OrderStatusEnum.values()) {
           if (enums.getCode().equals(status)) {
               return enums.getMsg();
           }
        }
        return "没有这种状态:status" + status;
    }
}
