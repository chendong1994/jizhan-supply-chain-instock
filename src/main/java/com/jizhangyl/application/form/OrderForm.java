package com.jizhangyl.application.form;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author 杨贤达
 * @date 2018/8/10 16:05
 * @description
 */
@Data
public class OrderForm {

    @NotNull(message = "发件人地址 id 不能为空")
    private Integer senderAddrId;

    @NotNull(message = "收件人地址 id 不能为空")
    private Integer receiverAddrId;

    @NotEmpty(message = "微信 openid 不能为空")
    private String openid;

    @NotEmpty(message = "购物车信息不能为空")
    private String items;
}
