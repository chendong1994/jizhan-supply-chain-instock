package com.jizhangyl.application.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jizhangyl.application.service.ExpenseCalendarService;
import com.jizhangyl.application.service.WalletService;


/**
 * 计算用户销售情况，返点情况的定时任务
 * @author 陈栋
 * @date 2018年9月18日  
 * @description
 */
@Component
public class CostUserExpenseTask {
	
	@Autowired
	private ExpenseCalendarService expenseCalendarService;
	@Autowired
	private WalletService walletService;
	
	
	/**
	 * 计算用户 上个月的销售情况和返点情况。以及计算这个月用户的权益等级
	 * 每个月1号00:01执行
	 */
	@Scheduled(cron = "0 1 0 1 * ?")
	public void calculate(){
		
		expenseCalendarService.calculate();
		
	}
	
	
	/**
	 * 根据物流状态更新订单状态,并且复制订单表到复制表
	 * 每天上午2：15触发
	 */
	@Scheduled(cron = "0 15 2 * * ?")
	public void updateOrderStatus(){
		expenseCalendarService.updateOrderStatus();
	}
	
	
	
	
	/**
	 * 每隔5分钟取消5分钟内未付款的订单
	 * 每隔5分钟触发
	 */
	@Scheduled(fixedRate = 1000*60*5)
	public void cancelOrder(){
		expenseCalendarService.cancelOrder();
	}
	
	/**
	 * 更新钱包
	 * 每月1号00:30分触发
	 */
	@Scheduled(cron = "0 30 0 1 * ?")
	public void updateUserWallet(){
		walletService.updateUserWallet();
	}
	
	
	
	

}
