package com.jizhangyl.application.form;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/7/27 10:43
 * @description
 */
@Data
public class ProductProviderForm {

    private Integer providerId;

    @NotEmpty(message = "公司日文名称不能为空")
    private String companyName;

    @NotEmpty(message = "公司名简称不能为空")
    private String companyAbbr;

    @NotEmpty(message = "公司地址不能为空")
    private String companyAddress;

    @NotEmpty(message = "公司法人不能为空")
    private String legalPerson;

    @NotNull(message = "公司注册资本不能为空")
    private BigDecimal registeredFund;

    @NotEmpty(message = "公司开户银行不能为空")
    private String depositBank;

    @NotEmpty(message = "银行账户不能为空")
    private String bankAccount;

    @NotEmpty(message = "采购联系人不能为空")
    private String purchaseContact;

    @NotEmpty(message = "联系电话不能为空")
    private String contactPhone;

    @NotNull(message = "货款账期不能为空")
    private Integer loanDate;

    @NotNull(message = "币种不能为空")
    private Integer currency;
}
