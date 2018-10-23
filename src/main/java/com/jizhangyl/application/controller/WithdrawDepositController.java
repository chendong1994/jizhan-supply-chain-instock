package com.jizhangyl.application.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.service.WithdrawDepositService;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户提现
 * @author 陈栋
 * @date 2018年9月20日  
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/deposit")
public class WithdrawDepositController {
	
	@Autowired
	private WithdrawDepositService withdrawDepositService;
	
	
	/**
	 * 小程序端提现，用post传输
	 * @param openid  小程序用户openid
	 * @param sum  提现金额
	 * @return
	 */
	@ResponseBody
	@PostMapping("/entPay")
	public ResultVO getWithdrawDeposit(@RequestParam("openid") String useropenid,@RequestParam("sum") Double sum,HttpServletRequest request){
		//1.入参校验
		if(StringUtils.isBlank(useropenid) || sum == null){
			throw new RuntimeException("参数为空");
		}
		//2.调用提现接口
		
		withdrawDepositService.getWithdrawDeposit(useropenid, sum,request);
		
		return ResultVOUtil.success();
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
