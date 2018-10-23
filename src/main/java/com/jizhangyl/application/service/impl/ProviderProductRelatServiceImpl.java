package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.VO.ProviderProductRelatVo;
import com.jizhangyl.application.dataobject.ProductProvider;
import com.jizhangyl.application.dataobject.ProviderProductRelat;
import com.jizhangyl.application.dataobject.Shop;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.ProviderProductRelatForm;
import com.jizhangyl.application.repository.ProviderProductRelatRepository;
import com.jizhangyl.application.service.ProductProviderService;
import com.jizhangyl.application.service.ProviderProductRelatService;
import com.jizhangyl.application.service.ShopService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/7/31 15:27
 * @description
 */
@Service
public class ProviderProductRelatServiceImpl implements ProviderProductRelatService {

    @Autowired
    private ProviderProductRelatRepository repository;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ProductProviderService productProviderService;

    @Autowired
    private ProviderProductRelatService providerProductRelatService;

    @Autowired
    private ProviderProductRelatRepository providerProductRelatRepository;

    @Override
    public Page<ProviderProductRelatVo> findByProviderId(Integer providerId,
                                                         Pageable pageable) {
        if (providerId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        Page<ProviderProductRelat> providerProductRelatPage = repository.findByProviderId(providerId, pageable);
        List<ProviderProductRelatVo> providerProductRelatVoList = providerProductRelatPage.getContent().stream()
                .map(e -> {
//                    e.getId(), e.getProviderId(), e.getProductId()
                    ProviderProductRelatVo providerProductRelatVo = new ProviderProductRelatVo();
                    BeanUtils.copyProperties(e, providerProductRelatVo);

                    Shop shop = shopService.findOne(e.getProductId());
                    if (shop != null) {
                        providerProductRelatVo.setBoxQuantity(shop.getShopXcount());
                        providerProductRelatVo.setProductJancode(shop.getShopJan());
                        providerProductRelatVo.setProductImage(shop.getShopImage());
                        // TODO
//                        providerProductRelatVo.setPurchasePrice();
//                        providerProductRelatVo.setPriceUpdateTime();
                    }
                    return providerProductRelatVo;
                }).collect(Collectors.toList());
        return new PageImpl<ProviderProductRelatVo>(providerProductRelatVoList, pageable, providerProductRelatPage.getTotalElements());
    }

    @Transactional
    @Override
    public ProviderProductRelat save(ProviderProductRelatForm providerProductRelatForm) {
        ProviderProductRelat providerProductRelat = new ProviderProductRelat();
        BeanUtils.copyProperties(providerProductRelatForm, providerProductRelat);

        Integer productId = providerProductRelat.getProductId();
        Shop shop = shopService.findOne(productId);
        if (shop != null) {
            providerProductRelat.setProductName(shop.getShopName());
            providerProductRelat.setProductJancode(shop.getShopJan());
            providerProductRelat.setBoxQuantity(shop.getShopXcount());
            providerProductRelat.setProductImage(shop.getShopImage());
        }

        ProviderProductRelat saveResult = repository.save(providerProductRelat);
        if (saveResult == null) {
            throw new GlobalException(ResultEnum.ADD_PRODUCT_ERROR);
        }
        return saveResult;
    }

    @Transactional
    @Override
    public ProviderProductRelat update(ProviderProductRelatForm providerProductRelatForm) {
//        ProviderProductRelat providerProductRelat = new ProviderProductRelat();
//        BeanUtils.copyProperties(providerProductRelatForm, providerProductRelat);

        ProviderProductRelat providerProductRelat = repository.findOne(providerProductRelatForm.getId());
        providerProductRelat.setPurchasePrice(providerProductRelatForm.getPurchasePrice());
        providerProductRelat.setProductStock(providerProductRelatForm.getProductStock());

        Integer productId = providerProductRelat.getProductId();
        Shop shop = shopService.findOne(productId);
        if (shop != null) {
            providerProductRelat.setProductName(shop.getShopName());
            providerProductRelat.setProductJancode(shop.getShopJan());
            providerProductRelat.setBoxQuantity(shop.getShopXcount());
            providerProductRelat.setProductImage(shop.getShopImage());
        }

        ProviderProductRelat updateResult = repository.save(providerProductRelat);
        if (updateResult == null) {
            throw new GlobalException(ResultEnum.UPDATE_PRODUCT_ERROR);
        }
        return updateResult;
    }

    @Override
    public List<ProviderProductRelatVo> findProviderByProductId(Integer productId) {
        List<ProviderProductRelatVo> providerProductRelatVoList = new ArrayList<>();
        List<ProviderProductRelat> providerProductRelatList = repository.findByProductId(productId);

        List<Integer> providerIdList = providerProductRelatList.stream().map(e -> e.getProviderId()).collect(Collectors.toList());
        List<ProductProvider> productProviderList = productProviderService.findByProviderIdIn(providerIdList);

        if (!CollectionUtils.isEmpty(providerProductRelatList)) {
            providerProductRelatVoList = providerProductRelatList.stream().map(e -> {
                ProviderProductRelatVo providerProductRelatVo = new ProviderProductRelatVo();
                BeanUtils.copyProperties(e, providerProductRelatVo);

                for (ProductProvider productProvider : productProviderList) {
                    if (e.getProviderId().equals(productProvider.getProviderId())) {
                        providerProductRelatVo.setProviderName(productProvider.getCompanyName());
                        break;
                    }
                }

                return providerProductRelatVo;
            }).collect(Collectors.toList());
        }
        return providerProductRelatVoList;
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        try {
            ProviderProductRelat providerProductRelat = repository.findOne(id);
            if (providerProductRelat == null) {
                throw new GlobalException(ResultEnum.REPOSITORY_DELETE_ERROR);
            }
            repository.delete(id);
        } catch (Exception e) {
            throw new GlobalException(ResultEnum.REPOSITORY_DELETE_ERROR);
        }
    }

    @Override
    public ProviderProductRelat findByProviderIdAndProductId(Integer productId, Integer providerId) {
        return repository.findByProviderIdAndProductId(providerId, productId);
    }


    /**
     * 查询供应商的商品
     * @param providerId
     * @param param
     * @return
     */
    @Override
    public List<ProviderProductRelatVo> findProviderShopByProductNameOrJancode(Integer providerId, String param) {
        List<ProviderProductRelat> providerProductRelatList = new ArrayList<>();
        List<ProviderProductRelatVo> providerProductRelatVoList = new ArrayList<>();
        // 按照 jancode 查询或者按照“产品名字”模糊查询
        ProviderProductRelat providerProductRelat = repository.findByProviderIdAndProductJancode(providerId, param);
        if (providerProductRelat == null) {
            List<ProviderProductRelat> list = repository.findByProviderIdAndProductNameLike(providerId, "%" + param + "%");
            if (!CollectionUtils.isEmpty(list)) {
                providerProductRelatList.addAll(list);
            }
        } else {
            providerProductRelatList.add(providerProductRelat);
        }
        if (!CollectionUtils.isEmpty(providerProductRelatList)) {
            providerProductRelatVoList = providerProductRelatList.stream().map(e -> {
                ProviderProductRelatVo providerProductRelatVo = new ProviderProductRelatVo();
                BeanUtils.copyProperties(e, providerProductRelatVo);
                return providerProductRelatVo;
            }).collect(Collectors.toList());
        }
        return providerProductRelatVoList;
    }

    @Override
    public List<ProviderProductRelat> getProductByJanOrName(String param) {
        return providerProductRelatRepository.findByProductNameOrProductJancodeLike(param);
    }

    @Override
    public List<ProviderProductRelat> getProductByJanOrName(Integer providerId, String param) {
        return providerProductRelatRepository.findProvidersProduct(providerId, param);
    }
}
