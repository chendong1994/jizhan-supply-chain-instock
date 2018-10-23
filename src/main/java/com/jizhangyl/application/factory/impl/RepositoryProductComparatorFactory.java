package com.jizhangyl.application.factory.impl;

import java.util.Comparator;
import java.util.Objects;

import com.jizhangyl.application.dataobject.RepositoryProduct;
import com.jizhangyl.application.enums.sort.OrderSortEnum;
import com.jizhangyl.application.enums.sort.RepositoryProductSortEnum;
import com.jizhangyl.application.factory.ComparatorFactory;

/**
 * @author 曲健磊
 * @date 2018年10月20日 下午6:37:15
 * @description 对仓库商品进行排序的比较器工厂
 */
public class RepositoryProductComparatorFactory extends ComparatorFactory<RepositoryProduct> {

    @Override
    public Comparator<RepositoryProduct> createComparator(Enum<?> paramEnum, Enum<?> orderEnum) {
        Objects.requireNonNull(paramEnum, "【创建仓库商品比较器】paramEnum=>null,比较器排序的基准不能为空");
        Objects.requireNonNull(orderEnum, "【创建仓库商品比较器】orderEnum=>null,比较器的升降序规则不能为空");

        RepositoryProductSortEnum repositoryShopSortEnum = (RepositoryProductSortEnum) paramEnum;
        OrderSortEnum orderSortEnum = (OrderSortEnum) orderEnum;

        switch(repositoryShopSortEnum) {
        case PACK_CODE: // 1.打包识别码
            return new Comparator<RepositoryProduct>(){
                @Override
                public int compare(RepositoryProduct o1, RepositoryProduct o2) {
                    return 0;
                }
            };
        case SHOP_STOCK: // 2.当前库存
            return new Comparator<RepositoryProduct>(){
                @Override
                public int compare(RepositoryProduct o1, RepositoryProduct o2) {
                    return 0;
                }
            };
        case OUT_NUM: // 3.即将出库数
            return new Comparator<RepositoryProduct>(){
                @Override
                public int compare(RepositoryProduct o1, RepositoryProduct o2) {
                    return 0;
                }
            };
        case YEST_OUT_NUM: // 4.昨日出库数
            return new Comparator<RepositoryProduct>(){
                @Override
                public int compare(RepositoryProduct o1, RepositoryProduct o2) {
                    return 0;
                }
            };
        case LASTWEEK_OUT_NUM: // 5.过去7天平均出库数
            return new Comparator<RepositoryProduct>(){
                @Override
                public int compare(RepositoryProduct o1, RepositoryProduct o2) {
                    return 0;
                }
            };
        default: // 默认的排序规则
            return new Comparator<RepositoryProduct>(){
                @Override
                public int compare(RepositoryProduct o1, RepositoryProduct o2) {
                    return 0;
                }
            };
        }
    }
}
