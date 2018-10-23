package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.Wxuser;
import com.jizhangyl.application.enums.WechatUserStatusEnum;

import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/2 16:23
 * @description
 */
public interface WxuserService {

    Wxuser findOne(Integer id);

    Wxuser findByOpenId(String openid);
    
    Wxuser findByUnionId(String unionid);

    WechatUserStatusEnum isExist(JSONObject obj);

    Boolean checkInviteCode(String code, String openid);

    Wxuser findByInviteCode(String inviteCode);

    List<Wxuser> findByOpenIdIn(List<String> openidList);

    List<Wxuser> findAll();

    List<Wxuser> findByInviteCodeNotNull();
}
