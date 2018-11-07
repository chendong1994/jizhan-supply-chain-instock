package com.jizhangyl.application.form;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author 杨贤达
 * @date 2018/8/20 17:42
 * @description
 */
@Data
public class PurchaseOrderConfirmForm {

    @NotEmpty(message = "订单号不能为空")
    private String orderId;

    @NotNull(message = "供应商不能为空")
    private Integer providerId;

    @NotEmpty(message = "订单商品详情不能为空")
    private String items;

    @NotEmpty(message = "收货凭证为空")
    private String certUrls;
}
