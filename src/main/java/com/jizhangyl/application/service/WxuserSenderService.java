package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.WxuserAddr;
import com.jizhangyl.application.dataobject.WxuserSender;
import com.jizhangyl.application.form.WxuserAddrForm;
import com.jizhangyl.application.form.WxuserSenderForm;
import com.jizhangyl.application.form.WxuserSenderUpdateForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/7 16:49
 * @description
 */
public interface WxuserSenderService {

    Page<WxuserSender> findAllByOpenid(String openid, Pageable pageable);

    List<WxuserSender> findAllByOpenid(String openid);

    void delete(String openid, Integer id);

    WxuserSender save(WxuserSenderForm wxuserSenderForm);

    WxuserSender update(WxuserSenderUpdateForm wxuserSenderUpdateForm);

    void setDefaultSender(String openid, Integer id);

    WxuserSender findOne(Integer senderId);

    WxuserSender findSenderAddrByOpenid(String openid);

    WxuserSender findDefault(String openid);

    List<WxuserSender> findByOpenidIn(List<String> openidList);
}
