package com.jizhangyl.application.VO;

import lombok.Data;

/**
 * @author 杨贤达
 * @date 2018/9/12 14:57
 * @description
 */
@Data
public class ProductImageVO {

    private String imageUrl;

    private Integer imageOrder;

    public ProductImageVO() {
    }

    public ProductImageVO(String imageUrl, Integer imageOrder) {
        this.imageUrl = imageUrl;
        this.imageOrder = imageOrder;
    }
}
