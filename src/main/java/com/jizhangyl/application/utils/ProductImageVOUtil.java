package com.jizhangyl.application.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jizhangyl.application.VO.ProductImageVO;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/12 17:22
 * @description
 */
@Slf4j
public class ProductImageVOUtil {

    public static List<ProductImageVO> convert(String items) {
        Gson gson = new Gson();
        List<ProductImageVO> productImageVOList = new ArrayList<>();

        try {
            productImageVOList = gson.fromJson(items, new TypeToken<List<ProductImageVO>>() {
            }.getType());
        } catch (Exception e) {
            log.error("【对象装换】错误, string = {}", items);
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(), e.getMessage()));
        }
        return productImageVOList;
    }
}
