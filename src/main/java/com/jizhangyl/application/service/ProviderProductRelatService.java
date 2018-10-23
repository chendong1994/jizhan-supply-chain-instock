package com.jizhangyl.application.service;

import com.jizhangyl.application.VO.ProviderProductRelatVo;
import com.jizhangyl.application.dataobject.ProviderProductRelat;
import com.jizhangyl.application.form.ProviderProductRelatForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/7/31 15:27
 * @description
 */
public interface ProviderProductRelatService {

    Page<ProviderProductRelatVo> findByProviderId(Integer providerId, Pageable pageable);

    ProviderProductRelat save(ProviderProductRelatForm providerProductRelatForm);

    ProviderProductRelat update(ProviderProductRelatForm providerProductRelatForm);

    void delete(Integer id);

    List<ProviderProductRelatVo> findProviderByProductId(Integer productId);

    ProviderProductRelat findByProviderIdAndProductId(Integer productId, Integer providerId);

    List<ProviderProductRelatVo> findProviderShopByProductNameOrJancode(Integer providerId, String param);

    List<ProviderProductRelat> getProductByJanOrName(String param);

    List<ProviderProductRelat> getProductByJanOrName(Integer providerId, String param);
}
