package com.jizhangyl.application.service;

import java.util.List;

import com.jizhangyl.application.dataobject.primary.ProviderProductLog;

/**
 * @author 曲健磊
 * @date 2018年10月19日 上午11:24:21
 * @description 供应商->商品->历史采购价+历史库存
 */
public interface ProviderProductLogService {

    /**
     * 保存采购价历史记录
     * @param providerProductLog
     */
    void save(ProviderProductLog providerProductLog);

    /**
     * 查询某个供应商的某个商品的历史采购价记录
     * @param providerId
     * @param productId
     * @return
     */
    List<ProviderProductLog> findPurchasePriceHistory(Integer providerId, Integer productId);

}
