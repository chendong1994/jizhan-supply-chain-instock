package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午3:53:48
 * @description 按照商品分类名称的拼音进行排序的比较器
 */
public class ShopCateComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public ShopCateComparator() {}

    public ShopCateComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }
    
    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        String cateName1 = o1.getCateName();
        String cateName2 = o2.getCateName();
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return cateName1.compareTo(cateName2);
        }
        return cateName2.compareTo(cateName1);
    }
}
