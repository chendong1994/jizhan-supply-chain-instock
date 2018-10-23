package com.jizhangyl.application.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/8/20 17:42
 * @description
 */
@Data
public class PurchaseOrderPayForm {

    @NotEmpty(message = "订单号不能为空")
    private String orderId;

    @NotNull(message = "供应商不能为空")
    private Integer providerId;

    @NotEmpty(message = "订单商品详情不能为空")
    private String items;

    @NotEmpty(message = "收货凭证为空")
    private String certUrls;

    @NotNull(message = "付款金额不能为空")
    private BigDecimal payAmount;

    /**
     * 备注
     */
    private String comment;
}
