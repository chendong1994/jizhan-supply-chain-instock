package com.jizhangyl.application.comparator.shop;

import java.math.BigDecimal;
import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月22日 上午10:03:25
 * @description 按照过去30天销量对商品进行排序的比较器
 */
public class LastMonthSalesNumComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public LastMonthSalesNumComparator() {}

    public LastMonthSalesNumComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }

    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        BigDecimal month1 = o1.getLastMonthSalesNum();
        BigDecimal month2 = o2.getLastMonthSalesNum();
        double m1 = 0.0;
        double m2 = 0.0;
        try {
            m1 = month1.doubleValue();
        } catch (NumberFormatException e) {
            m1 = 0.0;
        }
        try {
            m2 = month2.doubleValue();
        } catch (NumberFormatException e) {
            m2 = 0.0;
        }
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return m1 == m2 ? 0 : m1 > m2 ? 1 : -1;
        }
        return m1 == m2 ? 0 : m1 > m2 ? -1 : 1;
    }
}
