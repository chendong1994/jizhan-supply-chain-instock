package com.jizhangyl.application.service;


import com.jizhangyl.application.dataobject.ProductProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/7/27 9:19
 * @description
 */
public interface ProductProviderService {

    ProductProvider findOne(Integer providerId);

    Page<ProductProvider> findAll(Pageable pageable);

    List<ProductProvider> findAll();

    ProductProvider save(ProductProvider productProvider);

    void delete(Integer providerId);

    List<ProductProvider> findByProviderIdIn(List<Integer> providerIdList);

}
