package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.enums.sort.OrderSortEnum;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午4:58:58
 * @description 按照商品cc税率进行排序的比较器
 */
public class CcTaxRateComparator implements Comparator<ShopVO> {

    private OrderSortEnum orderSortEnum = OrderSortEnum.DESC; // 默认降序

    public CcTaxRateComparator() {}

    public CcTaxRateComparator(OrderSortEnum orderSortEnum) {
        this.orderSortEnum = orderSortEnum;
    }
    
    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        String c1 = o1.getTaxRate();
        String c2 = o2.getTaxRate();
        int cc1 = 0;
        int cc2 = 0;
        try { // 遇到税率为NaN的情况按0处理
            cc1 = Integer.valueOf(c1);
        } catch (NumberFormatException e) {
            cc1 = 0;
        }
        try {
            cc2 = Integer.valueOf(c2);
        } catch (NumberFormatException e) {
            cc2 = 0;
        }
        if (orderSortEnum.getCode().equals(OrderSortEnum.ASC.getCode())) { // 升序
            return cc1 == cc2 ? 0 : cc1 > cc2 ? 1 : -1;
        }
        return cc1 == cc2 ? 0 : cc1 > cc2 ? -1 : 1;
    }
}
