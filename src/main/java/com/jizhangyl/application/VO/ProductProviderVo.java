package com.jizhangyl.application.VO;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/7/27 10:00
 * @description
 */
@Data
public class ProductProviderVo implements Serializable {

    private static final long serialVersionUID = -320888278485855123L;

    private String companyName;

    private String companyAbbr;

    private String companyAddress;

    private String legalPerson;

    private BigDecimal registeredFund;

    private String depositBank;

    private String bankAccount;

    private String purchaseContact;

    private String contactPhone;

    private Integer loanDate;

    private Integer providerNo;

}
