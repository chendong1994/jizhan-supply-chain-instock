package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderDetail;
import com.jizhangyl.application.dataobject.primary.RepositoryProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/3 16:09
 * @description
 */
public interface RepositoryProductService {

    RepositoryProduct findByProductJancode(String productJancode);

    List<RepositoryProduct> findByProductJancodeIn(List<String> productJancodeList);

    RepositoryProduct save(RepositoryProduct repositoryProduct);

    Page<RepositoryProduct> findAll(Pageable pageable);

    void increaseProductStock(List<PurchaseOrderDetail> purchaseOrderDetailList);

    void increaseWayStock(List<PurchaseOrderDetail> purchaseOrderDetailList);

    void decreaseProductStock(List<PurchaseOrderDetail> purchaseOrderDetailList);

    void decreaseWayStock(List<PurchaseOrderDetail> purchaseOrderDetailList);

    List<RepositoryProduct> findByCriteria(String param, Integer status);

    Page<RepositoryProduct> findByCriteria(String param, Integer status, Pageable pageable);
}
