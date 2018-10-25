package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ProviderProductRelat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/7/31 15:15
 * @description
 */
public interface ProviderProductRelatRepository extends JpaRepository<ProviderProductRelat, Integer> {

    Page<ProviderProductRelat> findByProviderId(Integer providerId, Pageable pageable);

    List<ProviderProductRelat> findByProductId(Integer productId);

    List<ProviderProductRelat> findByProviderId(Integer providerId);

    ProviderProductRelat findByProviderIdAndProductId(Integer providerId, Integer productId);

    ProviderProductRelat findByProviderIdAndProductJancode(Integer providerId, String productJancode);

    List<ProviderProductRelat> findByProviderIdAndProductNameLike(Integer providerId, String name);

    @Query(nativeQuery = true, value = "SELECT * FROM provider_product_relat WHERE CONCAT(IFNULL(product_name,''),IFNULL(product_jancode,'')) LIKE %:param%")
    List<ProviderProductRelat> findByProductNameOrProductJancodeLike(@Param("param") String param);

    @Query(nativeQuery = true, value = "SELECT * FROM `provider_product_relat` WHERE `provider_id` = :providerId and CONCAT(IFNULL(`product_name`,''),IFNULL(`product_jancode`,'')) LIKE %:param%")
    List<ProviderProductRelat> findProvidersProduct(@Param("providerId") Integer providerId, @Param("param") String param);
}
