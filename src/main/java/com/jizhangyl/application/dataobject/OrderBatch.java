package com.jizhangyl.application.dataobject;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Data
@Entity
@DynamicUpdate
public class OrderBatch {
	
	@Id
	private String orderBatchId;
	
	private String openId;
	
	private BigDecimal orderAmountAll;
	
	private Date createTime;
	
	private Date updateTime;
	

}
