package com.jizhangyl.application.dataobject;

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
 * @description  代购每月销售金额汇总实体类
 */
@Data
@Entity
@DynamicUpdate
public class ExpenseCalendar {
	
	@Id
    @GeneratedValue
    private Integer expenseCalendarId;

    private String openId;//代购微信id

    private Integer userGrade;//权益等级
    
    private String userName;//用户名字

    private Date joinTime;//加入时间

    private BigDecimal salesDistribution;//分销返点比例
    
    private BigDecimal indirectSalesDistribution;//间接下游返点比例
    
    private BigDecimal expressDiscount;//物流折扣

    private BigDecimal productDiscount;//商品折扣

    private BigDecimal expenseSum;//用户自身每月的消费金额

    private String monthTime;//几几年几月
    
    private String inviteCode;//用户邀请码
    
    private String parentInviteCode;//上级邀请码
    
    
    private Integer downstreamPeople;//本月新增下游发展人数
    
    private BigDecimal downstreamSum;//本月下游的销售总金额
    
    private BigDecimal salesDistributionSum;//下游每月返点金额
    
    
    private Integer indirectDownstreamPeople;//本月新增间接下游人数
    
    private BigDecimal indirectDownstreamSum;//本月间接下游销售额总和 

    private BigDecimal indirectSalesDistributionSum;//下游间接每月返点金额
    
    private Date createTime;

    private Date updateTime;
    
    private String avatarUrl;
    
    
    
}