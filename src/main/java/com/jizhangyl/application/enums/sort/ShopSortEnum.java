package com.jizhangyl.application.enums.sort;

import lombok.Getter;

/**
 * @author 曲健磊
 * @date 2018年10月19日 下午3:16:43
 * @description 商品排序的指标
 */
@Getter
public enum ShopSortEnum {
    SUPPLY_PRICE(1, "供货价"),
    SHOP_STOCK(2, "商品库存"),
    SHOP_CATE(3, "商品分类"),
    SHOP_BRAND(4, "商品品牌"),
    SHOP_JWEIGHT(5, "商品净重量"),
    SHOP_DWEIGHT(6, "商品打包重量"),
    CC_CESS(7, "CC税率"),
    RETAIL_PRICE(8, "商品零售价"),
    YEST_SALESNUM(9, "昨日销量"),
    LASTWEEK_SALESNUM(10, "过去7天平均销量"),
    LASTMONTH_SALESNUM(11, "过去30天平均销量"),
    ;

    private Integer code;

    private String message;

    ShopSortEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
