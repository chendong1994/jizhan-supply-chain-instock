package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.ProductProvider;
import com.jizhangyl.application.dataobject.primary.ProviderProductRelat;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ProductProviderRepository;
import com.jizhangyl.application.repository.primary.ProviderProductRelatRepository;
import com.jizhangyl.application.service.ProductProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/7/27 9:25
 * @description
 */
@Slf4j
@Service
public class ProductProviderServiceImpl implements ProductProviderService {

    @Autowired
    private ProductProviderRepository productProviderRepository;

    @Autowired
    private ProviderProductRelatRepository providerProductRelatRepository;

    @Override
    public ProductProvider findOne(Integer providerId) {
        return productProviderRepository.findOne(providerId);
    }

    @Override
    public Page<ProductProvider> findAll(Pageable pageable) {
        return productProviderRepository.findAll(pageable);
    }

    @Override
    public List<ProductProvider> findAll() {
        return productProviderRepository.findAll();
    }

    @Override
    @Transactional
    public ProductProvider save(ProductProvider productProvider) {
        ProductProvider saveResult = productProviderRepository.save(productProvider);
        if (saveResult == null) {
            log.error("【新增供应商】新增失败: saveResult = {}", saveResult);
            throw new GlobalException(ResultEnum.PROVIDER_ADD_FAIL);
        }
        return saveResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Integer providerId) {
        try {
            productProviderRepository.delete(providerId);
            List<ProviderProductRelat> providerProductRelatList = providerProductRelatRepository.findByProviderId(providerId);
            providerProductRelatRepository.delete(providerProductRelatList);
        } catch (Exception e) {
            log.error("【供应商删除】删除失败: {}", e.getMessage());
            e.printStackTrace();
            throw new GlobalException(ResultEnum.PROVIDER_DELETE_ERROR);
        }
    }

    @Override
    public List<ProductProvider> findByProviderIdIn(List<Integer> providerIdList) {
        return productProviderRepository.findByProviderIdIn(providerIdList);
    }
}
