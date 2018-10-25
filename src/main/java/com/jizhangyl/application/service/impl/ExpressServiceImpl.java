package com.jizhangyl.application.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.VO.ExpressPreviewVo;
import com.jizhangyl.application.dataobject.primary.ExpressQueryHistory;
import com.jizhangyl.application.repository.primary.ExpressQueryHistoryRepository;
import com.jizhangyl.application.service.ExpressService;
import com.jizhangyl.application.utils.EMSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/24 11:33
 * @description
 */
@Slf4j
@Service
public class ExpressServiceImpl implements ExpressService {

    @Autowired
    private ExpressQueryHistoryRepository expressQueryHistoryRepository;

    @Autowired
    private EMSUtil emsUtil;

    /**
     * 首页下方历史记录列表
     * @param openid
     * @return
     */
    @Override
    public List<ExpressPreviewVo> findByOpenid(String openid) {
        List<ExpressQueryHistory> expressQueryHistoryList = expressQueryHistoryRepository.findByOpenidOrderByQueryDateDesc(openid);
//        List<String> expNumList = expressQueryHistoryList.stream()
//                .map(e -> e.getExpNum())
//                .collect(Collectors.toList());
        List<ExpressPreviewVo> expressPreviewVoList = expressQueryHistoryList.stream().map(e -> {
            ExpressPreviewVo expressPreviewVo = new ExpressPreviewVo();
            BeanUtils.copyProperties(e, expressPreviewVo);
            return expressPreviewVo;
        }).collect(Collectors.toList());

        for (ExpressPreviewVo expressPreviewVo : expressPreviewVoList) {
            String expNum = expressPreviewVo.getExpNum();
//            if (expNum.startsWith("BS") && expNum.endsWith("CN")) {
                JSONObject record = emsUtil.query(expNum);

                JSONArray jsonArray = record.getJSONArray("traces");
                if (jsonArray != null && jsonArray.size() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.size() - 1);
                    expressPreviewVo.setExpLastRecord(jsonObject);
                } else {
                    expressPreviewVo.setExpLastRecord(null);
                }
//            }
            /*else {
                JSONObject record = TrackerUtil.getTrace(expNum, "japan-post");
                JSONArray jsonArray = record.getJSONArray("trackinfo");
                if (jsonArray != null && jsonArray.size() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.size() - 1);
                    expressPreviewVo.setExpLastRecord(jsonObject);
                } else {
                    expressPreviewVo.setExpLastRecord(null);
                }
            }*/

        }
        return expressPreviewVoList;
    }

    /**
     * 查询详情
     * @param openid
     * @param expNum
     * @return
     */
    @Override
    public JSONObject query(String openid, String expNum) {
        // 1. 返回详情之前将该条查询记录入查询记录库
        ExpressQueryHistory expressQueryHistory = expressQueryHistoryRepository.findByOpenidAndExpNum(openid, expNum);
        if (expressQueryHistory == null) {
            ExpressQueryHistory history = new ExpressQueryHistory();
            history.setExpNum(expNum);
            history.setOpenid(openid);
            history.setQueryDate(new Date());
            expressQueryHistoryRepository.save(history);
        }

        // 2. 返回查询详情
//        if (expNum.startsWith("BS") && expNum.endsWith("CN")) {
            return emsUtil.query(expNum);
        /*} else {
            return TrackerUtil.getTrace(expNum, "japan-post");
        }*/

    }

    /**
     * 查询记录
     * @param openid
     * @return 历史查询的单号记录
     */
    @Override
    public List<String> findNumListByOpenid(String openid) {
        List<ExpressQueryHistory> expressQueryHistoryList = expressQueryHistoryRepository.findByOpenidOrderByQueryDateDesc(openid);
        List<String> expNumList = expressQueryHistoryList.stream().map(e -> e.getExpNum()).collect(Collectors.toList());
        return expNumList;
    }
}
