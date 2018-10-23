package com.jizhangyl.application.comparator.shop;

import java.util.Comparator;

import com.jizhangyl.application.VO.ShopVO;

/**
 * @author 曲健磊
 * @date 2018年10月22日 上午10:14:33
 * @description 商品默认的排序比较器(根据id进行排序)
 */
public class DefaultComparator implements Comparator<ShopVO> {

    @Override
    public int compare(ShopVO o1, ShopVO o2) {
        int id1 = o1.getId().intValue();
        int id2 = o2.getId().intValue();
        return id1 == id2 ? 0 : id1 > id2 ? 1 : -1;
    }
}
