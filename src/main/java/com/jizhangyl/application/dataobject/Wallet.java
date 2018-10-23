package com.jizhangyl.application.dataobject;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * 钱包实体类
 * @author 陈栋
 * @date 2018年10月16日  
 * @description
 */
@Data
@Entity
@DynamicUpdate
public class Wallet {
	
	
	@Id
    @GeneratedValue
    private Integer walletId;

    private String openId;

    private BigDecimal amount;//钱包金额

    private Integer status;//钱包状态0正常1冻结

    private Date createTime;

    private Date updateTime;

    
   
}