package com.jizhangyl.application.dataobject;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * 用户提现记录表
 * @author 陈栋
 * @date 2018年10月16日  
 * @description
 */
@Data
@Entity
@DynamicUpdate
public class WithdrawDeposit {
	
	
	@Id
    @GeneratedValue
    private Integer withdrawDepositId;

    private Integer walletId;

    private String openId;

    private BigDecimal withdrawAmount;//提现金额

    private Integer withdrawStatus;//提现状态0成功1失败

    private Date createTime;

    private Date updateTime;
    
    private String entpayId;//提现单号

    
}