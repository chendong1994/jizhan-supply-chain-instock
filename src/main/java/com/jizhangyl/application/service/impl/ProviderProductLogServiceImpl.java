package com.jizhangyl.application.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jizhangyl.application.dataobject.primary.ProviderProductLog;
import com.jizhangyl.application.repository.primary.ProviderProductLogRepository;
import com.jizhangyl.application.service.ProviderProductLogService;

/**
 * @author 曲健磊
 * @date 2018年10月19日 上午11:24:59
 * @description 供应商->商品->历史采购价+历史库存
 */
@Service
public class ProviderProductLogServiceImpl implements ProviderProductLogService {

    @Autowired
    private ProviderProductLogRepository providerProductLogRepository;

    /**
     * 保存采购价历史记录
     * @param providerProductLog
     */
    @Override
    public void save(ProviderProductLog providerProductLog) {
        providerProductLogRepository.save(providerProductLog);
    }

    /**
     * 查询某个供应商的某个商品的历史采购价记录
     * @param providerId
     * @param productId
     * @return
     */
    @Override
    public List<ProviderProductLog> findPurchasePriceHistory(Integer providerId, Integer productId) {
        Objects.requireNonNull(providerId, "【查询供应商商品历史采购价记录】供应商id为空");
        Objects.requireNonNull(productId, "【查询供应商商品历史采购价记录】商品id为空");
        List<ProviderProductLog> historyList = providerProductLogRepository.findByProviderIdAndProductId(providerId, productId);
        return historyList;
    }
}
