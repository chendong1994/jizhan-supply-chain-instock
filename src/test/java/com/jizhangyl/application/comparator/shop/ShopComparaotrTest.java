package com.jizhangyl.application.comparator.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.converter.Status2EnumConverter;
import com.jizhangyl.application.enums.sort.OrderSortEnum;
import com.jizhangyl.application.enums.sort.ShopSortEnum;
import com.jizhangyl.application.factory.ComparatorFactory;
import com.jizhangyl.application.factory.impl.ShopVOComparatorFactory;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午4:19:03
 * @description 测试对商品进行排序的各种比较器
 */
public class ShopComparaotrTest {

    /**
     * (0).测试按照默认规则进行排序的比较器
     */
    @Test
    public void testDefaultComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setId(100);
        s2.setId(78);
        s3.setId(89);
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 12; // 商品过去7天销量(10)
        Integer ascDesc = 1; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getId());
        }
    }
    
    /**
     * (1).测试根据商品的供货价进行排序的比较器
     */
    @Test
    public void testSupplyPriceComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setShopGprice(BigDecimal.valueOf(99.0));
        s2.setShopGprice(BigDecimal.valueOf(4.0));
        s3.setShopGprice(BigDecimal.valueOf(10000.0));
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 1; // 供货价(1)
        Integer ascDesc = 1; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getShopGprice());
        }
    }
    
    /**
     * (2).测试根据商品的库存进行排序的比较器
     */
    @Test
    public void testShopStockComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setShopCount(4);
        s2.setShopCount(1);
        s3.setShopCount(5);
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 2; // 商品库存(2)
        Integer ascDesc = 2; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getShopCount());
        }
    }
    
    /**
     * (3).测试根据商品的分类进行排序的比较器
     */
    @Test
    public void testShopCateComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setCateName("彩妆"); // c
        s2.setCateName("面膜"); // m
        s3.setCateName("洁面"); // j
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 3; // 商品分类(3)
        Integer ascDesc = 2; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getCateName());
        }
    }

    /**
     * (4).测试根据商品的品牌分类进行排序的比较器
     */
    @Test
    public void testShopBrandComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setBrandName("Cow");
        s2.setBrandName("DHC");
        s3.setBrandName("Elegance");
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 4; // 商品品牌(4)
        Integer ascDesc = 2; // 降序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getBrandName());
        }
    }
    
    /**
     * (5).测试根据商品的净重量进行排序的比较器
     */
    @Test
    public void testShopJWeightComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setShopJweight(2);
        s2.setShopJweight(1);
        s3.setShopJweight(9);
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 5; // 商品净重量(5)
        Integer ascDesc = 2; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getShopJweight());
        }
    }
    
    /**
     * (6).测试根据商品的打包重量进行排序的比较器
     */
    @Test
    public void testShopDWeightComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setShopDweight(2);
        s2.setShopDweight(1);
        s3.setShopDweight(9);
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 6; // 商品打包重量(6)
        Integer ascDesc = 2; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getShopDweight());
        }
    }
    
    /**
     * (7).测试根据商品的cc税率进行排序的比较器
     */
    @Test
    public void testCcTaxRateComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        ShopVO s4 = new ShopVO();
        ShopVO s5 = new ShopVO();
        s1.setTaxRate("NaN");
        s2.setTaxRate("NaN");
        s3.setTaxRate("25");
        s4.setTaxRate("NaN");
        s5.setTaxRate("35");
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        shopList.add(s4);
        shopList.add(s5);
        
        Integer shopStatus = 7; // 商品cc税率(7)
        Integer ascDesc = 1; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getTaxRate());
        }
    }
    
    /**
     * (8).测试根据商品的零售价进行排序的比较器
     */
    @Test
    public void testShopLPriceComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        ShopVO s4 = new ShopVO();
        ShopVO s5 = new ShopVO();
        s1.setShopLprice(BigDecimal.valueOf(2.0));
        s2.setShopLprice(BigDecimal.valueOf(12.0));
        s3.setShopLprice(BigDecimal.valueOf(66.0));
        s4.setShopLprice(BigDecimal.valueOf(1.0));
        s5.setShopLprice(BigDecimal.valueOf(999.0));
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        shopList.add(s4);
        shopList.add(s5);
        
        Integer shopStatus = 8; // 商品零售价(8)
        Integer ascDesc = 2; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getShopLprice());
        }
    }
    
    /**
     * (9).测试根据商品的昨日销量进行排序的比较器
     */
    @Test
    public void testYestSalesNumComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setYestSalesNum(BigDecimal.valueOf(8.0));
        s2.setYestSalesNum(BigDecimal.valueOf(1.0));
        s3.setYestSalesNum(BigDecimal.valueOf(1.0));
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 9; // 商品昨日销量(9)
        Integer ascDesc = 1; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getYestSalesNum().doubleValue());
        }
    }
    
    /**
     * (10).测试根据商品的过去7天销量进行排序的比较器
     */
    @Test
    public void testLastWeekSalesNumComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setLastWeekSalesNum(BigDecimal.valueOf(8.0));
        s2.setLastWeekSalesNum(BigDecimal.valueOf(2.0));
        s3.setLastWeekSalesNum(BigDecimal.valueOf(1.0));
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 10; // 商品过去7天销量(10)
        Integer ascDesc = 1; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getLastWeekSalesNum().doubleValue());
        }
    }
    
    /**
     * (11).测试根据商品的过去30天销量进行排序的比较器
     */
    @Test
    public void testMonthWeekSalesNumComparator() {
        List<ShopVO> shopList = new ArrayList<>();
        ShopVO s1 = new ShopVO();
        ShopVO s2 = new ShopVO();
        ShopVO s3 = new ShopVO();
        s1.setLastMonthSalesNum(BigDecimal.valueOf(8.0));
        s2.setLastMonthSalesNum(BigDecimal.valueOf(1.0));
        s3.setLastMonthSalesNum(BigDecimal.valueOf(1.0));
        
        shopList.add(s1);
        shopList.add(s2);
        shopList.add(s3);
        
        Integer shopStatus = 11; // 商品过去7天销量(10)
        Integer ascDesc = 1; // 升序
        
        ShopSortEnum se = Status2EnumConverter.trans2ShopSort(shopStatus);
        OrderSortEnum os = Status2EnumConverter.trans2OrderSort(ascDesc);
        ComparatorFactory<ShopVO> comparatorFactory = new ShopVOComparatorFactory();
        Comparator<ShopVO> shopComparator = comparatorFactory.createComparator(se, os);
        Collections.sort(shopList, shopComparator);
        
        for (ShopVO shopVO : shopList) {
            System.out.println(shopVO.getLastMonthSalesNum().doubleValue());
        }
    }
}
