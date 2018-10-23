package com.jizhangyl.application.converter;

import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.dataobject.Shop;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:20
 * @description
 */
public class Shop2ShopVOConverter {

    public static ShopVO convert(Shop shop) {
        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shop, shopVO);
        return shopVO;
    }

    public static List<ShopVO> convert(List<Shop> shopList) {
        List<ShopVO> shopVOList = shopList.stream()
                .map(e -> convert(e))
                .collect(Collectors.toList());
        return shopVOList;
    }
}
