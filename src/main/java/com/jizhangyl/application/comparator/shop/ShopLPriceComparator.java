package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午6:08:55
 * @description 按照商品零售价进行排序的比较器
 */
public class ShopLPriceComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public ShopLPriceComparator() {}

    public ShopLPriceComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }

    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        double l1 = o1.getShopLprice().doubleValue();
        double l2 = o2.getShopLprice().doubleValue();
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return l1 == l2 ? 0 : l1 > l2 ? 1 : -1;
        }
        return l1 == l2 ? 0 : l1 > l2 ? -1 : 1;
    }
}
