package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/11 16:14
 * @description
 */
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProductId(Integer productId);

    void deleteByProductId(Integer productId);
}
