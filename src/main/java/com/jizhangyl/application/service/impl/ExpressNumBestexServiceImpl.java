package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.primary.ExpressNumBestex;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpressNumBestexRepository;
import com.jizhangyl.application.service.ExpressNumBestexService;
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
public class ExpressNumBestexServiceImpl implements ExpressNumBestexService {

    @Autowired
    private ExpressNumBestexRepository expressNumBestexRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
        expressNumBestexRepository.delete(id);
    }

    @Override
    public ExpressNumBestex save(ExpressNumBestex expressNumBestex) {
        return expressNumBestexRepository.save(expressNumBestex);
    }

    /**
     * 批量入库
     * @param expressNumBestexList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumBestex> saveInBatch(List<ExpressNumBestex> expressNumBestexList) {
        List<ExpressNumBestex> dataList = new ArrayList<>();

        for (ExpressNumBestex expressNumBestex : expressNumBestexList) {
            if (dataList.size() == 1000) {
                expressNumBestexRepository.save(dataList);
                dataList.clear();
            }
            dataList.add(expressNumBestex);
        }
        if (!dataList.isEmpty()) {
            expressNumBestexRepository.save(dataList);
        }
        return expressNumBestexList;
    }

    @Override
    public ExpressNumBestex findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumBestexRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
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
        ExpressNumBestex expressNumBestex = expressNumBestexRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumBestex;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
        ExpressNumBestex expressNumBestex = expressNumBestexRepository.findOne(id);
        expressNumBestex.setStatus(code);
        expressNumBestexRepository.save(expressNumBestex);
    }

    @Override
    public ExpressNumBestex findOne(Integer id) {
        return expressNumBestexRepository.findOne(id);
    }

    @Override
    public ExpressNumBestex findByExpNum(String expNum) {
        return expressNumBestexRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumBestex> findAll() {
        return expressNumBestexRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumBestexRepository.countByStatus(status);
    }
}
