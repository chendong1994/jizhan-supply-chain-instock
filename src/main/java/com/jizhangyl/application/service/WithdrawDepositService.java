package com.jizhangyl.application.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户提现
 * @author 陈栋
 * @date 2018年9月21日  
 * @description
 */
public interface WithdrawDepositService {
	
	/**
	 * <pre>
	 * 用户提现
	 * 
	 * 微信系统提现限制：
	 * 1.不支持给非实名用户打款  
	 * 2.给同一个实名用户付款，单笔单日限额2W/2W (最低1元)
	 * 3.一个商户同一日付款总额限额100W 
	 * 4.默认每天最多可向同一个用户付款10次，可以在商户平台--API安全进行设置
	 * 5.付款金额必须小于或等于商户当前可用余额的金额
	 * 
	 * </pre>
	 * 
	 * @param useropenid 提现人openid
	 * @param sum  提现金额（单位元）
	 */
	public void  getWithdrawDeposit(String useropenid,double sum,HttpServletRequest request);
	
	
	
	
	
	
	

}
