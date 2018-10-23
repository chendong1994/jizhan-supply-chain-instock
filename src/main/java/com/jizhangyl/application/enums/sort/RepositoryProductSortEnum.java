package com.jizhangyl.application.enums.sort;

import lombok.Getter;

/**
 * @author 曲健磊
 * @date 2018年10月19日 下午3:34:24
 * @description 仓库商品排序指标
 */
@Getter
public enum RepositoryProductSortEnum {
    PACK_CODE(1, "打包识别码"),
    SHOP_STOCK(2, "当前库存"),
    OUT_NUM(3, "即将出库数"),
    YEST_OUT_NUM(4, "昨日出库数"),
    LASTWEEK_OUT_NUM(5, "过去7天平均出库数"),
    ;

    private Integer code;

    private String message;

    RepositoryProductSortEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
