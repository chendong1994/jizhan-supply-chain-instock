package com.jizhangyl.application.VO;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/8/3 16:20
 * @description
 */
@Data
public class CartVO implements Serializable {

    private static final long serialVersionUID = 5148868651796117787L;

    private Integer id;

    private Integer productId;

    private String productName;

    private BigDecimal productPrice;

    private Integer productQuantity;

    private Integer productStatus;

    /**
     * 以下属性从 shop 表中查询获取
     */

    private String shopImage;

    private Integer shopDweight;

    private BigDecimal shopGprice;

    private BigDecimal shopLprice;
}
