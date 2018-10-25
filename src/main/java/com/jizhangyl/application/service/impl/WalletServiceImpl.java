package com.jizhangyl.application.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jizhangyl.application.dataobject.primary.ExpenseCalendar;
import com.jizhangyl.application.dataobject.primary.Wallet;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.enums.WalletStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpenseCalendarRepository;
import com.jizhangyl.application.repository.primary.WalletRepository;
import com.jizhangyl.application.repository.secondary.WxuserRepository;
import com.jizhangyl.application.service.WalletService;
import com.jizhangyl.application.utils.DateUtil;

@Service
public class WalletServiceImpl implements WalletService {

	@Autowired
	private WalletRepository walletRepository;
	@Autowired
	private ExpenseCalendarRepository expenseCalendarRepository;
	@Autowired
	private WxuserRepository wxuserRepository;
	
	
	@Override
	public void updateUserWallet() {

		//查询所有已经激活的用户钱包（不查询用户，因为用户在天邀请码的时候已经激活钱包）
		List<Wallet> listWallet = walletRepository.findByStatus(WalletStatusEnum.OPEN.getCode());
        //查询上个月expense_calendar表的数据
		List<ExpenseCalendar> listExpenseCalendar = expenseCalendarRepository.findByMonthTime(DateUtil.getFrontMonth());
		
		for (Wallet wallet : listWallet) {
			BigDecimal amo = BigDecimal.ZERO;
			for(ExpenseCalendar expenseCalendar : listExpenseCalendar){
				if(wallet.getOpenId().equals(expenseCalendar.getOpenId())){
					amo = amo.add(expenseCalendar.getSalesDistributionSum()).add(expenseCalendar.getIndirectSalesDistributionSum());
				}
			}
			wallet.setAmount(amo);
		}
		walletRepository.save(listWallet);
	}

	
	@Override
	public Wallet addWallet(Wallet wallet) {
		if (wallet == null || StringUtils.isBlank(wallet.getOpenId())) {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
		Wallet wa = walletRepository.findByOpenId(wallet.getOpenId());
		if (wa != null) {
			throw new GlobalException(ResultEnum.PARAM_ERROR);
		}
		return walletRepository.saveAndFlush(wallet);
	}


	@Override
	public Boolean verifyUserWallet(String openId) {
		Wallet wa = walletRepository.findByOpenId(openId);
		if(wa != null && wa.getStatus().equals(WalletStatusEnum.OPEN.getCode())){
			return true;
		}
		return false;
	}


	@Override
	public void gg() {
		 List<Wxuser>  list = wxuserRepository.findByInviteCodeNotNull();
		 for(Wxuser wxuser : list){
			 Wallet wallet = new Wallet();
			 wallet.setAmount(BigDecimal.ZERO);
			 wallet.setOpenId(wxuser.getOpenId());
			 wallet.setStatus(WalletStatusEnum.OPEN.getCode());
			 
			 this.addWallet(wallet);
			 
		 }
		 
		
	}
	
	
	
	
	
	

}
