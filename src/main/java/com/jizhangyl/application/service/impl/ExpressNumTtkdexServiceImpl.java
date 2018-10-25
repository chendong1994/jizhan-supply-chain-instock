package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.primary.ExpressNumTtkdex;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpressNumTtkdexRepository;
import com.jizhangyl.application.service.ExpressNumTtkdexService;
import com.jizhangyl.application.service.RedisLock;
import com.jizhangyl.application.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:50
 * @description
 */
@Slf4j
@Service
public class ExpressNumTtkdexServiceImpl implements ExpressNumTtkdexService {

    @Autowired
    private ExpressNumTtkdexRepository expressNumTtkdexRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
        expressNumTtkdexRepository.delete(id);
    }

    @Override
    public ExpressNumTtkdex save(ExpressNumTtkdex expressNumTtkdex) {
        return expressNumTtkdexRepository.save(expressNumTtkdex);
    }

    /**
     * 批量入库
     * @param expressNumTtkdexList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumTtkdex> saveInBatch(List<ExpressNumTtkdex> expressNumTtkdexList) {
        List<ExpressNumTtkdex> dataList = new ArrayList<>();

        for (ExpressNumTtkdex expressNumTtkdex : expressNumTtkdexList) {
            if (dataList.size() == 1000) {
                expressNumTtkdexRepository.save(dataList);
                dataList.clear();
            }
            dataList.add(expressNumTtkdex);
        }
        if (!dataList.isEmpty()) {
            expressNumTtkdexRepository.save(dataList);
        }
        return expressNumTtkdexList;
    }

    @Override
    public ExpressNumTtkdex findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumTtkdexRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
        log.warn("【飞翩物流】剩余可用单号: {} 个", remainSize);
        if (remainSize < 2) {
            // 触发短信操作 -> 18516184686
            SendSmsResponse response = smsUtil.remainNotify("18516184686", remainSize, null);
            if (response.getCode() != null && response.getCode().equals("OK")) {
                log.info("【飞翩物流】提醒短信发送成功");
            } else {
                log.error("【飞翩物流】提醒短信发送失败");
            }
            log.warn("【飞翩物流】单号即将用尽, 请尽快补充, 仅剩: {} 个", remainSize);
        }
        ExpressNumTtkdex expressNumTtkdex = expressNumTtkdexRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumTtkdex;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
        ExpressNumTtkdex expressNumTtkdex = expressNumTtkdexRepository.findOne(id);
        expressNumTtkdex.setStatus(code);
        expressNumTtkdexRepository.save(expressNumTtkdex);
    }

    @Override
    public ExpressNumTtkdex findOne(Integer id) {
        return expressNumTtkdexRepository.findOne(id);
    }

    @Override
    public ExpressNumTtkdex findByExpNum(String expNum) {
        return expressNumTtkdexRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumTtkdex> findAll() {
        return expressNumTtkdexRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumTtkdexRepository.countByStatus(status);
    }
}
