package com.jizhangyl.application.comparator.shop;

import java.math.BigDecimal;
import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月22日 上午9:49:14
 * @description 按照过去7天销量对商品进行排序的比较器
 */
public class LastWeekSalesNumComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public LastWeekSalesNumComparator() {}

    public LastWeekSalesNumComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }
    
    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        BigDecimal w1 = o1.getLastWeekSalesNum();
        BigDecimal w2 = o2.getLastWeekSalesNum();
        double last1 = 0.0;
        double last2 = 0.0;
        try {
            last1 = w1.doubleValue();
        } catch (NumberFormatException e) {
            last1 = 0.0;
        }
        try {
            last2 = w2.doubleValue();
        } catch (NumberFormatException e) {
            last2 = 0.0;
        }
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return last1 == last2 ? 0 : last1 > last2 ? 1 : -1;
        }
        return last1 == last2 ? 0 : last1 > last2 ? -1 : 1;
    }
}
