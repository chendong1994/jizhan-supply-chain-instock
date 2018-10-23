package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午4:14:53
 * @description 按照商品净重量进行排序的比较器
 */
public class ShopJWeightComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public ShopJWeightComparator() {}

    public ShopJWeightComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }

    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        int jweight1 = o1.getShopJweight().intValue();
        int jweight2 = o2.getShopJweight().intValue();
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return jweight1 == jweight2 ? 0 : jweight1 > jweight2 ? 1 : -1;
        }
        return jweight1 == jweight2 ? 0 : jweight1 > jweight2 ? -1 : 1;
    }
}
