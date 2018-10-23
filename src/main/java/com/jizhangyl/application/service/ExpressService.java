package com.jizhangyl.application.service;

import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.VO.ExpressPreviewVo;
import com.jizhangyl.application.dataobject.ExpressQueryHistory;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/24 11:30
 * @description
 */
public interface ExpressService {

    List<ExpressPreviewVo> findByOpenid(String openid);

    JSONObject query(String openid, String expNum);

    List<String> findNumListByOpenid(String openid);
}
