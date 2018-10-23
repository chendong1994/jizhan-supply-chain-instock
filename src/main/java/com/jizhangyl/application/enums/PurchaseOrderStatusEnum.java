package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/7/26 18:12
 * @description
 */
@Getter
public enum PurchaseOrderStatusEnum implements CodeEnum {
    NEW(0, "已下单"),
    ACCESSED(1, "已入库（全部）"),
    PAID(2, "已打款（全额）"),
    PAID_PART(3, "已打款（部分）"),
    CANCEL(4, "已取消"),
    ACCESSED_PART(5, "已入库（部分）"),
    // 已下采购订单，且对方已经确认
    CONFIRMED(6, "已确认"),
    FINISHED(7, "已完结"),
    ;

    private Integer code;

    private String msg;

    PurchaseOrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
