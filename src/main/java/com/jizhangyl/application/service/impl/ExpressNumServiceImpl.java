package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.ExpressNum;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.ExpressNumRepository;
import com.jizhangyl.application.service.ExpressNumService;
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
public class ExpressNumServiceImpl implements ExpressNumService {

    @Autowired
    private ExpressNumRepository expressNumRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
        expressNumRepository.delete(id);
    }

    @Override
    public ExpressNum save(ExpressNum expressNum) {
        return expressNumRepository.save(expressNum);
    }

    /**
     * 批量入库
     * @param expressNumList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNum> saveInBatch(List<ExpressNum> expressNumList) {
        List<ExpressNum> dataList = new ArrayList<>();

        for (ExpressNum expressNum : expressNumList) {
            if (dataList.size() == 1000) {
                expressNumRepository.save(dataList);
                dataList.clear();
            }
            dataList.add(expressNum);
        }
        if (!dataList.isEmpty()) {
            expressNumRepository.save(dataList);
        }
        return expressNumList;
    }

    @Override
    public ExpressNum findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
        log.warn("【飞翩物流】剩余可用单号: {} 个", remainSize);
        if (remainSize < 3000) {
            // 触发短信操作 -> 18516184686
            SendSmsResponse response = smsUtil.remainNotify("18516184686", remainSize, null);
            if (response.getCode() != null && response.getCode().equals("OK")) {
                log.info("【飞翩物流】提醒短信发送成功");
            } else {
                log.error("【飞翩物流】提醒短信发送失败");
            }
            log.warn("【飞翩物流】单号即将用尽, 请尽快补充, 仅剩: {} 个", remainSize);
        }
        ExpressNum expressNum = expressNumRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNum;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
        ExpressNum expressNum = expressNumRepository.findOne(id);
        expressNum.setStatus(code);
        expressNumRepository.save(expressNum);
    }

    @Override
    public ExpressNum findOne(Integer id) {
        return expressNumRepository.findOne(id);
    }

    @Override
    public ExpressNum findByExpNum(String expNum) {
        return expressNumRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNum> findAll() {
        return expressNumRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumRepository.countByStatus(status);
    }
}
