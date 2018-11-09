package com.jizhangyl.application.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.jizhangyl.application.dataobject.primary.Wallet;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.enums.WalletStatusEnum;
import com.jizhangyl.application.enums.WechatUserStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.OrderMasterRepository;
import com.jizhangyl.application.repository.secondary.WxuserRepository;
import com.jizhangyl.application.service.WalletService;
import com.jizhangyl.application.service.WxuserService;
import com.jizhangyl.application.utils.KeyUtil;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

/**
 * @author 杨贤达
 * @date 2018/8/2 16:24
 * @description
 */
@Slf4j
@Service
public class WxuserServiceImpl implements WxuserService {
	
	@Autowired
	private WxuserRepository wxuserRepository;
	
	@Autowired
	private OrderMasterRepository orderMasterRepository;
	
	@Autowired
	private WalletService walletService;

    @Override
    public Wxuser findOne(Integer id) {
        if (id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
//        return wxuserRepository.findOne(id);
        return wxuserRepository.getOne(id);
    }

    @Override
    public Wxuser findByOpenId(String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return wxuserRepository.findByOpenId(openid);
    }

    @Override
    public Wxuser findByUnionId(String unionid) {
        if (StringUtils.isEmpty(unionid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return wxuserRepository.findByUnionId(unionid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized WechatUserStatusEnum isExist(JSONObject obj) {

        // 判断用户是否已经存在
        if (obj == null) {
            throw new GlobalException(ResultEnum.WECHAT_ERROR);
        }

        String openid = (String) obj.get("openId");
        String unionId = (String) obj.get("unionId");

        if (unionId != null) {
            log.info("【$$$$$$$$$$$$$$$$$】");
            log.info("【$$$$$$$$$$$$$$$$$】");
            log.info("【$$$$$ 小程序端的unionId:" + unionId + " $$$$$】");
            log.info("【$$$$$$$$$$$$$$$$$】");
            log.info("【$$$$$$$$$$$$$$$$$】");
        } else {
            log.info("【$$$$$$$$$$$$$$$$$】");
            log.info("【$$$$$$$$$$$$$$$$$】");
            log.info("【$$$$$ 小程序端没有获取到unionId $$$$$】");
            log.info("【$$$$$$$$$$$$$$$$$】");
            log.info("【$$$$$$$$$$$$$$$$$】");
        }
        
        Wxuser wxuser = wxuserRepository.findByUnionId(unionId);
        if (wxuser != null) {
            if (StringUtils.isEmpty(wxuser.getUnionId())) { // 更新一下该用户的unionId
                wxuser.setUnionId(unionId);
                wxuserRepository.save(wxuser);
            }
            Short status = wxuser.getIsActivate();
            if (status.equals(WechatUserStatusEnum.NOT_ACTIVATE.getCode().shortValue())) {
                return WechatUserStatusEnum.NOT_ACTIVATE;
            } else if (status.equals(WechatUserStatusEnum.ACTIVATED.getCode().shortValue())) {
                return WechatUserStatusEnum.ACTIVATED;
            }
        } else {
            Wxuser record = new Wxuser();
            record.setOpenId(openid);
            record.setUnionId(unionId); // 新增一下用户的unionid
            record.setNickName((String) obj.get("nickName"));
            record.setGender(Short.parseShort(obj.get("gender").toString()));
            record.setLanguage((String) obj.get("language"));
            record.setCity((String) obj.get("city"));
            record.setProvince((String) obj.get("province"));
            record.setCountry((String) obj.get("country"));
            record.setAvatarUrl((String) obj.get("avatarUrl"));
            record.setIsActivate(WechatUserStatusEnum.NOT_ACTIVATE.getCode().shortValue());
            record.setCreateTime(new Date());

            Wxuser result = wxuserRepository.save(record);
            if (result == null) {
                throw new GlobalException(ResultEnum.DB_ERROR);
            }
            return WechatUserStatusEnum.NEW;

        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean checkInviteCode(String code, String openid) {
        Wxuser wxuser = findByOpenId(openid);
        if (wxuser == null) {
            throw new GlobalException(ResultEnum.USER_NOT_EXIST);
        }
        // 查询邀请码的提供者
        Wxuser user = this.findByInviteCode(code);
        if (user == null) {
            throw new GlobalException(ResultEnum.INVITE_CODE_NOT_EXIST);
        }

        String newInviteCode = KeyUtil.genKey();
        Wxuser wu = findByInviteCode(newInviteCode);
        while (wu != null) {
            newInviteCode = KeyUtil.genKey();
            wu = findByInviteCode(newInviteCode);
        }
        // 设置新用户邀请码
        wxuser.setInviteCode(newInviteCode);
        // 设置新用户的父级邀请码
        wxuser.setParentInviteCode(code);
        wxuser.setIsActivate(WechatUserStatusEnum.ACTIVATED.getCode().shortValue());
        wxuserRepository.save(wxuser);
        
        //设置用户钱包初始化
        Wallet wallet = new Wallet();
        wallet.setAmount(BigDecimal.ZERO);
        wallet.setOpenId(openid);
        wallet.setStatus(WalletStatusEnum.OPEN.getCode());
        walletService.addWallet(wallet);
        
        
        return true;
    }

    @Override
    public Wxuser findByInviteCode(String inviteCode) {
        return wxuserRepository.findByInviteCode(inviteCode);
    }

    @Override
    public List<Wxuser> findByOpenIdIn(List<String> openidList) {
        return wxuserRepository.findByOpenIdIn(openidList);
    }

    @Override
    public List<Wxuser> findAll() {
        return wxuserRepository.findAll();
    }

    @Override
    public List<Wxuser> findByInviteCodeNotNull() {
        return wxuserRepository.findByInviteCodeNotNull();
    }
}
