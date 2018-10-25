package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.primary.ExpressNumYundaex;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpressNumYundaexRepository;
import com.jizhangyl.application.service.ExpressNumYundaexService;
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
public class ExpressNumYundaexServiceImpl implements ExpressNumYundaexService {

    @Autowired
    private ExpressNumYundaexRepository expressNumYundaexRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
        expressNumYundaexRepository.delete(id);
    }

    @Override
    public ExpressNumYundaex save(ExpressNumYundaex expressNumYundaex) {
        return expressNumYundaexRepository.save(expressNumYundaex);
    }

    /**
     * 批量入库
     * @param expressNumYundaexList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumYundaex> saveInBatch(List<ExpressNumYundaex> expressNumYundaexList) {
        List<ExpressNumYundaex> dataList = new ArrayList<>();

        for (ExpressNumYundaex expressNumYundaex : expressNumYundaexList) {
            if (dataList.size() == 1000) {
                expressNumYundaexRepository.save(dataList);
                dataList.clear();
            }
            dataList.add(expressNumYundaex);
        }
        if (!dataList.isEmpty()) {
            expressNumYundaexRepository.save(dataList);
        }
        return expressNumYundaexList;
    }

    @Override
    public ExpressNumYundaex findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumYundaexRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
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
        ExpressNumYundaex expressNumYundaex = expressNumYundaexRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumYundaex;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
        ExpressNumYundaex expressNumYundaex = expressNumYundaexRepository.findOne(id);
        expressNumYundaex.setStatus(code);
        expressNumYundaexRepository.save(expressNumYundaex);
    }

    @Override
    public ExpressNumYundaex findOne(Integer id) {
        return expressNumYundaexRepository.findOne(id);
    }

    @Override
    public ExpressNumYundaex findByExpNum(String expNum) {
        return expressNumYundaexRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumYundaex> findAll() {
        return expressNumYundaexRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumYundaexRepository.countByStatus(status);
    }
}
