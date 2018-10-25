package com.jizhangyl.application.dataobject.primary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jizhangyl.application.enums.CurrencyEnum;
import com.jizhangyl.application.utils.EnumUtil;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/7/27 9:07
 * @description 供应商实体类
 */
@Data
@Entity
@DynamicUpdate
public class ProductProvider {

    @Id
    @GeneratedValue
    private Integer providerId;

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

    private Integer currency = CurrencyEnum.JPY.getCode();

    private Date createTime;

    private Date updateTime;

    @Column(name = "ext_1")
    private String ext1;

    @Column(name = "ext_2")
    private String ext2;

    @Column(name = "ext_3")
    private String ext3;

    @JsonIgnore
    public CurrencyEnum getCurrencyEnum() {
        return EnumUtil.getByCode(currency, CurrencyEnum.class);
    }
}
