package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午4:34:48
 * @description 按照商品打包重量进行排序的比较器
 */
public class ShopDWeightComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public ShopDWeightComparator() {}

    public ShopDWeightComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }

    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        int dweight1 = o1.getShopDweight().intValue();
        int dweight2 = o2.getShopDweight().intValue();
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return dweight1 == dweight2 ? 0 : dweight1 > dweight2 ? 1 : -1;
        }
        return dweight1 == dweight2 ? 0 : dweight1 > dweight2 ? -1 : 1;
    }
}
