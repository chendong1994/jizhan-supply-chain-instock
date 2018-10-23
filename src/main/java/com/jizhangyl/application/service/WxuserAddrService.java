package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.WxuserAddr;
import com.jizhangyl.application.form.WxuserAddrForm;
import com.jizhangyl.application.form.WxuserAddrUpdateForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/5 13:09
 * @description
 */
public interface WxuserAddrService {

    List<WxuserAddr> findAll();

    Page<WxuserAddr> findAllByOpenid(String openid, Pageable pageable);

    List<WxuserAddr> findAllByOpenid(String openid);

    int saveBatch(List<WxuserAddr> wxuserAddrList);

    void delete(String openid, Integer id);

    WxuserAddr save(WxuserAddrForm wxuserAddrForm);

    WxuserAddr save(WxuserAddr wxuserAddr);

    WxuserAddr update(WxuserAddrUpdateForm wxuserAddrUpdateForm);

    WxuserAddr findOne(Integer addrId);

    List<WxuserAddr> findByIdIn(List<Integer> idList);
}
