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
public class PurchaseOrderForm {

    private String orderId;

    @NotNull(message = "供应商不能为空")
    private Integer providerId;

    @NotNull(message = "贷款账期不能为空")
    private Integer loanDate;

    @NotEmpty(message = "订单商品详情不能为空")
    private String items;

    /*@NotNull(message = "币种不能为空")
    private Integer currency;*/
}
