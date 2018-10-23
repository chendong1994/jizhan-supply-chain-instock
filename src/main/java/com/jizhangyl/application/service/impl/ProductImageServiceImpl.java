package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.VO.ProductImageVO;
import com.jizhangyl.application.dataobject.ProductImage;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.ProductImageRepository;
import com.jizhangyl.application.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/9/11 16:15
 * @description
 */
@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    /**
     * 商品详情图入库
     * @param productId
     * @param productImageVOList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Integer productId, List<ProductImageVO> productImageVOList) {

        // 删除该商品所有相关图片
        productImageRepository.deleteByProductId(productId);

        // 图片列表入库
        List<ProductImage> productImageList = new ArrayList<>();

        for (ProductImageVO productImageVO : productImageVOList) {
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(productImageVO.getImageUrl());
            productImage.setImageOrder(productImageVO.getImageOrder());
            productImage.setProductId(productId);
            productImageList.add(productImage);
        }

        if (!CollectionUtils.isEmpty(productImageList)) {
            List<ProductImage> result = productImageRepository.save(productImageList);
            if (CollectionUtils.isEmpty(result)) {
                throw new GlobalException(ResultEnum.PRODUCT_IMAGE_ADD_FAIL);
            }
        }

        /*List<ProductImage> existedProductImageList = productImageRepository.findByProductId(productId);

        List<ProductImage> productImageList = new ArrayList<>();

        for (ProductImageVO productImageVO : productImageVOList) {
            Integer outerOrder = productImageVO.getImageOrder();
            String outerUrl = productImageVO.getImageUrl();
            boolean isExist = false;
            for (ProductImage productImage : existedProductImageList) {
                Integer innerOrder = productImage.getImageOrder();
                String innerUrl = productImage.getImageUrl();

                // 当找到相同次序的详情图记录
                // 如果对应序号的图片已存在且不相等则替换，不存在直接入库
                if (outerOrder != null && innerOrder != null && outerOrder.equals(innerOrder)) {
                    isExist = true;
                    if ((StringUtils.isEmpty(innerUrl) && !StringUtils.isEmpty(outerUrl)) ||
                            (!StringUtils.isEmpty(outerUrl) && !StringUtils.isEmpty(innerUrl) && !outerUrl.equals(innerUrl))) {
                        productImage.setImageUrl(outerUrl);
                        productImageList.add(productImage);
                    }
                    break;
                }
            }
            if (!isExist) {
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(productImageVO.getImageUrl());
                productImage.setImageOrder(productImageVO.getImageOrder());
                productImage.setProductId(productId);
                productImageList.add(productImage);
            }
        }

        if (!CollectionUtils.isEmpty(productImageList)) {
            List<ProductImage> result = productImageRepository.save(productImageList);
            if (CollectionUtils.isEmpty(result)) {
                throw new GlobalException(ResultEnum.PRODUCT_IMAGE_ADD_FAIL);
            }
        }*/
    }

    @Override
    public List<ProductImageVO> imageList(Integer productId) {

        List<ProductImageVO> productImageVOList = new ArrayList<>();

        List<ProductImage> productImageList = productImageRepository.findByProductId(productId);
        if (!CollectionUtils.isEmpty(productImageList)) {
            productImageVOList = productImageList.stream()
                    .map(e -> new ProductImageVO(e.getImageUrl(), e.getImageOrder()))
                    .collect(Collectors.toList());
        }
        return productImageVOList;
    }
}
