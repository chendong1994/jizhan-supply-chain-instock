package com.jizhangyl.application.service.impl;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.jizhangyl.application.config.WechatAccountConfig;
import com.jizhangyl.application.dataobject.primary.Wallet;
import com.jizhangyl.application.dataobject.primary.WithdrawDeposit;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.enums.WalletStatusEnum;
import com.jizhangyl.application.enums.WithdrawStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.WalletRepository;
import com.jizhangyl.application.repository.primary.WithdrawDepositRepository;
import com.jizhangyl.application.repository.secondary.WxuserRepository;
import com.jizhangyl.application.service.WithdrawDepositService;
import com.jizhangyl.application.utils.IpUtil;
import com.jizhangyl.application.utils.KeyUtil;
import com.jizhangyl.application.utils.PayUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class WithdrawDepositServiceImpl implements WithdrawDepositService{
	
	@Autowired
    private WithdrawDepositRepository  withdrawDepositRepository;
	@Autowired
    private WalletRepository walletRepository;
	@Autowired
    private WxPayService wxPayService;
	@Autowired
    private WechatAccountConfig accountConfig;
	@Autowired
    private WxuserRepository wxuserRepository;
	

	//==========做方法锁，简单的安全控制，流程异常做回滚=========
	@Override
	@Transactional(rollbackFor = Exception.class)
	public synchronized void  getWithdrawDeposit(String openid, double sum,HttpServletRequest req) {
		
		//验证用户状态
		Wxuser wxuser = wxuserRepository.findByOpenId(openid);
		if(wxuser == null){
			throw new GlobalException(ResultEnum.USER_NOT_EXIST);
		}
		//验证钱包数据
		Wallet wallet = walletRepository.findByOpenId(openid);
		if(wallet == null || wallet.getStatus().equals(WalletStatusEnum.CLOSE.getCode())){
			throw new GlobalException(ResultEnum.NOT_ACTIVATE_WALLET);
		}
		//提现金额不能大于钱包金额
		if(wallet.getAmount().compareTo(BigDecimal.valueOf(sum)) < 0){
			throw new RuntimeException("提现金额不能大于账户可返点金额");
		}
		//其他金额校验限制，微信端会返回错误对应信息
		
		//申请商户号的appid
		String mch_appid = accountConfig.getMpAppId();
		//商户号
		String mchid = accountConfig.getMchId();
		//随机字符串
		String nonceStr = PayUtil.getNonceStr();
		//商户订单号
		String partner_trade_no = "jz"+KeyUtil.genUniqueKey();
		//用户openid
//		openid = "oRIP940h7_hjQhFI__r7QX-ar_YQ";
		//校验用户姓名选项
		String check_name = "NO_CHECK";
		//金额
		Integer amount = (int) (100*sum);
		//企业付款描述信息
		String desc = "返点提现";
		//Ip地址
		String  spbill_create_ip = IpUtil.getIp(req);
		
		String params ="amount="+amount+"check_name="+check_name+"desc="+desc+"mch_appid="+mch_appid+
				"mchid="+mchid+"nonce_str="+nonceStr+"openid="+openid+"partner_trade_no="+partner_trade_no+
				"spbill_create_ip="+spbill_create_ip+"key="+"4JoJRXXJuPnYpu4jWvDu3dz3pNPdTbbF";
		//签名
		String paySign = DigestUtils.md5Hex(params).toUpperCase();
		EntPayRequest request = new EntPayRequest();
		request.setMchAppid(mch_appid);
		request.setMchId(mchid);
		request.setNonceStr(nonceStr);
		request.setPartnerTradeNo(partner_trade_no);
		request.setOpenid(openid);
		request.setCheckName(check_name);
		request.setAmount(amount);
		request.setDescription(desc);
		request.setSpbillCreateIp(spbill_create_ip);
		request.setSign(paySign);
		try {
			EntPayResult entPayResult = wxPayService.getEntPayService().entPay(request);
			//钱包扣钱
			wallet.setAmount(wallet.getAmount().subtract(BigDecimal.valueOf(sum)));
			walletRepository.save(wallet);
			//写入提现记录表
			WithdrawDeposit withdrawDeposit = new WithdrawDeposit();
			withdrawDeposit.setOpenId(openid);
			withdrawDeposit.setEntpayId(partner_trade_no);
			withdrawDeposit.setWalletId(wallet.getWalletId());
			withdrawDeposit.setWithdrawAmount(BigDecimal.valueOf(sum));
			withdrawDeposit.setWithdrawStatus(WithdrawStatusEnum.SUCCEED.getCode());
			withdrawDepositRepository.save(withdrawDeposit);
			
		} catch (WxPayException e) {
			log.error("错误信息:"+e.getXmlString());
			throw new RuntimeException("提现失败");
		}
		
		
	}
	
	
	
	
	
	

}
