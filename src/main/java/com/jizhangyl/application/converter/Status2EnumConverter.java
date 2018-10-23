package com.jizhangyl.application.converter;

import com.jizhangyl.application.enums.sort.OrderSortEnum;
import com.jizhangyl.application.enums.sort.RepositoryProductSortEnum;
import com.jizhangyl.application.enums.sort.ShopSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月20日 下午10:11:32
 * @description 将数字转换为对应的枚举类对象(排序传参所需)
 */
public class Status2EnumConverter {

    /**
     * 将升降序代号转换成对应的枚举
     * @param status
     * @return
     */
    public static OrderSortEnum trans2OrderSort(Integer status) {
        for (OrderSortEnum e : OrderSortEnum.values()) {
            if (e.getCode().equals(status)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 将商品排序参数代号转换成对应的枚举
     * @param status
     * @return
     */
    public static ShopSortEnum trans2ShopSort(Integer status) {
        for (ShopSortEnum e : ShopSortEnum.values()) {
            if (e.getCode().equals(status)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 将仓库商品排序参数代号转换成对应的枚举
     * @param status
     * @return
     */
    public static RepositoryProductSortEnum trans2RepositoryProductSort(Integer status) {
        for (RepositoryProductSortEnum e : RepositoryProductSortEnum.values()) {
            if (e.getCode().equals(status)) {
                return e;
            }
        }
        return null;
    }
}
