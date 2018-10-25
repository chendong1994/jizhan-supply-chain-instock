package com.jizhangyl.application.converter;

import com.jizhangyl.application.VO.RepositoryProductVO;
import com.jizhangyl.application.dataobject.primary.Shop;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/23 15:49
 * @description
 */
public class Shop2RepositoryProductVoConverter {

    public static RepositoryProductVO convert(Shop shop) {
        RepositoryProductVO productVo = new RepositoryProductVO();
        productVo.setProductJancode(shop.getShopJan());
        productVo.setProductName(shop.getShopName());
        productVo.setProductStock(shop.getShopCount());
        return productVo;
    }

    public static List<RepositoryProductVO> convert(List<Shop> shopList) {
        return shopList.stream()
                .map(e -> convert(e))
                .collect(Collectors.toList());
    }
}
