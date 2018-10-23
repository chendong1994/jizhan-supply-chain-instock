package com.jizhangyl.application.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ExpenseCalendarDto {
	
	private String url ;//头像地址 
	
	private String userName;//名字
	
	private BigDecimal expenseSum;//用户自身每月的消费金额
	
	private BigDecimal sum;//返点给上级的金额
	
	private Integer userGrade;//用户等级
	
	private String inviteCode;//用户邀请码
	
	private Integer downPeople;//下游人员人数
	
	

}
