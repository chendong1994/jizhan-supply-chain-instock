package com.jizhangyl.application.repository;


import com.jizhangyl.application.dataobject.ProductProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/7/27 9:09
 * @description
 */
public interface ProductProviderRepository extends JpaRepository<ProductProvider, Integer> {

    List<ProductProvider> findByProviderIdIn(List<Integer> providerIdList);

}
