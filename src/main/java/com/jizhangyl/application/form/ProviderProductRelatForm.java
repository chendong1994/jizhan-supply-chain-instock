package com.jizhangyl.application.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/8/17 15:26
 * @description
 */
@Data
public class ProviderProductRelatForm {

    private Integer id;

    @NotNull(message = "供应商id不能为空")
    private Integer providerId;

    @NotNull(message = "商品id不能为空")
    private Integer productId;

    @NotNull(message = "商品供货价格不能为空")
    private BigDecimal purchasePrice;

    @NotNull(message = "商品库存不能为空")
    private Integer productStock;
}
