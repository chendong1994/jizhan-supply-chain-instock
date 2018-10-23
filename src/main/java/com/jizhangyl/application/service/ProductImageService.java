package com.jizhangyl.application.service;

import com.jizhangyl.application.VO.ProductImageVO;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/11 16:15
 * @description
 */
public interface ProductImageService {

    void save(Integer productId, List<ProductImageVO> productImageVOList);

    List<ProductImageVO> imageList(Integer productId);
}
