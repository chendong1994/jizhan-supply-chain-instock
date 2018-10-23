package com.jizhangyl.application.enums;

import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/10/12 20:28
 * @description
 */
@Getter
public enum CurrencyEnum implements CodeEnum {
    JPY(0, "日元"),
    CNY(1, "人民币"),
    USD(2, "美元"),
    EUR(3, "欧元"),
    HKD(4, "港币"),
    GBP(5, "英镑"),
    KRW(6, "韩元"),
    CAD(7, "加元"),
    AUD(8, "澳元"),
    CHF(9, "瑞郎"),
    SGD(10, "新加坡元"),
    MYR(11, "马来西亚币"),
    IDR(12, "印尼"),
    NZD(13, "新西兰"),
    VND(14, "越南"),
    THB(15, "泰铢"),
    PHP(16, "菲律宾")
    ;

    private Integer code;

    private String currency;

    CurrencyEnum(Integer code, String currency) {
        this.code = code;
        this.currency = currency;
    }
}
