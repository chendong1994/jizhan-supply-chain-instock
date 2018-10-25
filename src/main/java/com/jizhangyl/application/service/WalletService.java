package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.Wallet;

/**
 * 用户钱包
 * @author 陈栋
 * @date 2018年10月16日  
 * @description
 */
public interface WalletService {
	
	/**
	 * 定时任务专用（更新用户钱包,结算上个月用户返点金额）
	 */
	void updateUserWallet();
	
	/**
	 * 用户钱包初始化
	 * @param wallet
	 * @return
	 */
	Wallet addWallet(Wallet wallet);
	
	
	/**
	 * 验证用户钱包是否已经激活<br>
	 * true 激活，false 没激活
	 * @param openId
	 * @return
	 */
	Boolean verifyUserWallet(String openId);
	
	/**
	 * 添加全部
	 */
	void gg();
	

}
