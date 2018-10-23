package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月20日 下午7:07:40
 * @description 按照商品的供货价进行排序的比较器
 */
public class SupplyPriceComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public SupplyPriceComparator() {}

    public SupplyPriceComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }

    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        double g1 = o1.getShopGprice().doubleValue();
        double g2 = o2.getShopGprice().doubleValue();
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return g1 == g2 ? 0 : g1 > g2 ? 1 : -1;
        } // 降序
        return g1 == g2 ? 0 : g1 > g2 ? -1 : 1;
    }
}
