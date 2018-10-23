package com.jizhangyl.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/8/1 19:57
 * @description
 */
@Data
public class CartDTO {

    private Integer id;

    private Integer productId;

    private String productName;

    private BigDecimal productPrice;

    private Integer productQuantity;

    private Integer productStatus;
}
