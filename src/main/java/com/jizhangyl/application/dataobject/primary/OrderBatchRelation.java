package com.jizhangyl.application.dataobject.primary;

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
public class OrderBatchRelation {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderBatchRelationId;

	private String orderBatchId;
	
	private String orderId;
	
	private Date createTime;
	
	private Date updateTime;

}
