package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderDetail;
import com.jizhangyl.application.dataobject.primary.RepositoryProduct;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.RepositoryProductRepository;
import com.jizhangyl.application.service.RepositoryProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/3 16:10
 * @description
 */
@Slf4j
@Service
public class RepositoryProductServiceImpl implements RepositoryProductService {

    @Autowired
    private RepositoryProductRepository repositoryProductRepository;

    @Override
    public RepositoryProduct findByProductJancode(String productJancode) {
        return repositoryProductRepository.findByProductJancode(productJancode);
    }

    @Override
    public List<RepositoryProduct> findByProductJancodeIn(List<String> productJancodeList) {
        return repositoryProductRepository.findByProductJancodeIn(productJancodeList);
    }

    @Override
    public RepositoryProduct save(RepositoryProduct repositoryProduct) {
        return repositoryProductRepository.save(repositoryProduct);
    }

    @Override
    public Page<RepositoryProduct> findAll(Pageable pageable) {
        return repositoryProductRepository.findAll(pageable);
    }

    /**
     * 加仓库商品当前库存
     * @param purchaseOrderDetailList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void increaseProductStock(List<PurchaseOrderDetail> purchaseOrderDetailList) {
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetailList) {
            RepositoryProduct repositoryProduct = repositoryProductRepository.findByProductId(purchaseOrderDetail.getProductId());
            if (repositoryProduct == null) {
                throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer result = repositoryProduct.getProductStock() + purchaseOrderDetail.getProductQuantity();
            repositoryProduct.setProductStock(result);
            repositoryProductRepository.save(repositoryProduct);
        }
    }

    /**
     * 加在途库存
     * @param purchaseOrderDetailList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void increaseWayStock(List<PurchaseOrderDetail> purchaseOrderDetailList) {
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetailList) {
            RepositoryProduct repositoryProduct = repositoryProductRepository.findByProductId(purchaseOrderDetail.getProductId());
            if (repositoryProduct == null) {
                throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer result = repositoryProduct.getWayStock() + purchaseOrderDetail.getProductQuantity();
            repositoryProduct.setWayStock(result);
            repositoryProductRepository.save(repositoryProduct);
        }
    }

    /**
     * 减当前库存
     * @param purchaseOrderDetailList
     */
    @Override
    public void decreaseProductStock(List<PurchaseOrderDetail> purchaseOrderDetailList) {
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetailList) {
            RepositoryProduct repositoryProduct = repositoryProductRepository.findByProductId(purchaseOrderDetail.getProductId());
            if (repositoryProduct == null) {
                throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST.getCode(),
                        ResultEnum.PRODUCT_NOT_EXIST.getMessage() + "productId = " + purchaseOrderDetail.getProductId());
            }

            Integer result = repositoryProduct.getProductStock() - purchaseOrderDetail.getProductQuantity();
            if (result < 0) {
                log.error("【扣库存】错误, Jancode = {}", purchaseOrderDetail.getProductJancode());
                throw new GlobalException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH.getCode(), "商品库存不足, Jancode = " + repositoryProduct.getProductJancode());
            }
            repositoryProduct.setProductStock(result);
            repositoryProductRepository.save(repositoryProduct);
        }
    }

    /**
     * 扣在途库存
     * @param purchaseOrderDetailList
     */
    @Override
    public void decreaseWayStock(List<PurchaseOrderDetail> purchaseOrderDetailList) {
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetailList) {
            RepositoryProduct repositoryProduct = repositoryProductRepository.findByProductId(purchaseOrderDetail.getProductId());
            if (repositoryProduct == null) {
                throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            Integer result = repositoryProduct.getWayStock() - purchaseOrderDetail.getProductQuantity();
            if (result < 0) {
                log.error("【扣库存】错误, Jancode = {}", purchaseOrderDetail.getProductJancode());
                throw new GlobalException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH.getCode(), "商品库存不足, Jancode = " + purchaseOrderDetail.getProductJancode());
            }
            repositoryProduct.setWayStock(result);
            repositoryProductRepository.save(repositoryProduct);
        }
    }

    @Override
    public List<RepositoryProduct> findByCriteria(String param, Integer status) {
        return repositoryProductRepository.findByCriteria(param, status);
    }

    @Override
    public Page<RepositoryProduct> findByCriteria(String param, Integer status, Pageable pageable) {
        return repositoryProductRepository.findByCriteria(param, status, pageable);
    }
}
