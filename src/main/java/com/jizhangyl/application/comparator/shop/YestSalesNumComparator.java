package com.jizhangyl.application.comparator.shop;

import java.math.BigDecimal;
import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月22日 上午9:37:46
 * @description 按照昨日销量对商品进行排序的比较器
 */
public class YestSalesNumComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public YestSalesNumComparator() {}

    public YestSalesNumComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }

    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        BigDecimal y1 = o1.getYestSalesNum();
        BigDecimal y2 = o2.getYestSalesNum();
        double salesNum1 = 0.0;
        double salesNum2 = 0.0;
        try {
            salesNum1 = y1.doubleValue();
        } catch (NumberFormatException e) {
            salesNum1 = 0.0;
        }
        try {
            salesNum2 = y2.doubleValue();
        } catch (NumberFormatException e) {
            salesNum2 = 0.0;
        }
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return salesNum1 == salesNum2 ? 0 : salesNum1 > salesNum2 ? 1 : -1;
        }
        return salesNum1 == salesNum2 ? 0 : salesNum1 > salesNum2 ? -1 : 1;
    }
}
