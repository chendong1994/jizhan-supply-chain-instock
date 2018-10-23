package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.VO.ProductImageVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author 杨贤达
 * @date 2018/9/12 18:56
 * @description
 */
@Component
public class ProductImageServiceImplTest extends MainApplicationTests {

    @Autowired
    private ProductImageServiceImpl productImageService;

    @Test
    public void save() {
        Integer productId = 1;
        List<ProductImageVO > productImageVOList = new ArrayList<ProductImageVO>() {
            {
                add(new ProductImageVO("http://xxy.jpg", 1));
                add(new ProductImageVO("http://bba.jpg", 4));
                add(new ProductImageVO("http://aaa.jpg", 5));
                add(new ProductImageVO("http://ccc.jpg", 6));
            }};
        productImageService.save(productId, productImageVOList);

    }

    @Test
    public void imageList() {
    }
}