package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午4:03:26
 * @description 按照商品品牌名称进行排序的比较器
 */
public class ShopBrandComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public ShopBrandComparator() {}

    public ShopBrandComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }
    
    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        String brandName1 = o1.getBrandName();
        String brandName2 = o2.getBrandName();
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return brandName1.compareTo(brandName2);
        }
        return brandName2.compareTo(brandName1);
    }
}
