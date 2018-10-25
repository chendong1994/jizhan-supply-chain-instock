package com.jizhangyl.application.dataobject.primary;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * 
 * @author 陈栋
 * @date 2018年9月17日  
 * @description 代购权益实体类
 */
@Data
@Entity
@DynamicUpdate
public class Interests {
	
	@Id
    @GeneratedValue
    private Integer interestsId;

    private Integer userGrade;//权益等级

    private BigDecimal salesDistribution;//分销返点比例
    
    private BigDecimal indirectSalesDistribution;//间接下游返点比例

    private BigDecimal expressDiscount;//物流折扣

    private BigDecimal productDiscount;//商品折扣

    private Date createTime;
    
    private Date updateTime;

   
}