package com.jizhangyl.application.factory.impl;

import java.util.Comparator;
import java.util.Objects;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.comparator.shop.CcTaxRateComparator;
import com.jizhangyl.application.comparator.shop.DefaultComparator;
import com.jizhangyl.application.comparator.shop.LastMonthSalesNumComparator;
import com.jizhangyl.application.comparator.shop.LastWeekSalesNumComparator;
import com.jizhangyl.application.comparator.shop.ShopBrandComparator;
import com.jizhangyl.application.comparator.shop.ShopCateComparator;
import com.jizhangyl.application.comparator.shop.ShopDWeightComparator;
import com.jizhangyl.application.comparator.shop.ShopJWeightComparator;
import com.jizhangyl.application.comparator.shop.ShopLPriceComparator;
import com.jizhangyl.application.comparator.shop.ShopStockComparator;
import com.jizhangyl.application.comparator.shop.SupplyPriceComparator;
import com.jizhangyl.application.comparator.shop.YestSalesNumComparator;
import com.jizhangyl.application.enums.sort.OrderSortEnum;
import com.jizhangyl.application.enums.sort.ShopSortEnum;
import com.jizhangyl.application.factory.ComparatorFactory;

/**
 * @author 曲健磊
 * @date 2018年10月20日 下午6:13:42
 * @description 对商品进行排序的比较器工厂(简单工厂)
 */
public class ShopVOComparatorFactory extends ComparatorFactory<ShopVO> {

    @Override
    public Comparator<ShopVO> createComparator(Enum<?> paramEnum, Enum<?> orderEnum) {
        Objects.requireNonNull(paramEnum, "【创建商品比较器】paramEnum=>null,比较器排序的基准不能为空");
        Objects.requireNonNull(orderEnum, "【创建商品比较器】orderEnum=>null,比较器的升降序规则不能为空");

        ShopSortEnum shopSortEnum = (ShopSortEnum) paramEnum;
        OrderSortEnum orderSortEnum = (OrderSortEnum) orderEnum;

        switch(shopSortEnum) {
        case SUPPLY_PRICE: // 1.供货价
            return new SupplyPriceComparator(orderSortEnum);
        case SHOP_STOCK: // 2.商品库存
            return new ShopStockComparator(orderSortEnum);
        case SHOP_CATE: // 3.商品分类
            return new ShopCateComparator(orderSortEnum);
        case SHOP_BRAND: // 4.商品品牌
            return new ShopBrandComparator(orderSortEnum);
        case SHOP_JWEIGHT: // 5.商品净重量
            return new ShopJWeightComparator(orderSortEnum);
        case SHOP_DWEIGHT: // 6.商品打包重量
            return new ShopDWeightComparator(orderSortEnum);
        case CC_CESS: // 7.CC税率
            return new CcTaxRateComparator(orderSortEnum);
        case RETAIL_PRICE: // 8.商品零售价
            return new ShopLPriceComparator(orderSortEnum);
        case YEST_SALESNUM: // 9.昨日销量
            return new YestSalesNumComparator(orderSortEnum);
        case LASTWEEK_SALESNUM: // 10.过去7天平均销量
            return new LastWeekSalesNumComparator(orderSortEnum);
        case LASTMONTH_SALESNUM: // 11.过去30天平均销量
            return new LastMonthSalesNumComparator(orderSortEnum);
        default: // 默认的排序规则(根据id)
            return new DefaultComparator();
        }
    }
}
