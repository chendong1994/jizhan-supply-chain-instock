package com.jizhangyl.application.dataobject.primary;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;


@Data
@Entity
@DynamicUpdate
public class OaBill {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oaBillId;

    private Integer billType;

    private BigDecimal billAmount;

    private String remark;

    private Date createTime;

    private Date updateTime;

    
}