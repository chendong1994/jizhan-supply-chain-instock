package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午3:32:08
 * @description 按照商品库存进行排序的比较器
 */
public class ShopStockComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public ShopStockComparator() {}

    public ShopStockComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }
    
    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        int s1 = o1.getShopCount().intValue();
        int s2 = o2.getShopCount().intValue();
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return s1 == s2 ? 0 : s1 > s2 ? 1 : -1;
        } // 降序
        return s1 == s2 ? 0 : s1 > s2 ? -1 : 1;
    }
}
