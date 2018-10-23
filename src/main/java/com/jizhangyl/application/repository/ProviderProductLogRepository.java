package com.jizhangyl.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.ProviderProductLog;

/**
 * @author 曲健磊
 * @date 2018年10月19日 上午11:22:37
 * @description 供应商->商品->历史采购价+历史库存
 */
public interface ProviderProductLogRepository extends JpaRepository<ProviderProductLog, Integer> {

    List<ProviderProductLog> findByProviderIdAndProductId(Integer providerId, Integer productId);
}
