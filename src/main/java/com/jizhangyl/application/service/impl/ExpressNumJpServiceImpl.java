package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.ExpressNumJp;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.ExpressNumJpRepository;
import com.jizhangyl.application.service.ExpressNumJpService;
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
public class ExpressNumJpServiceImpl implements ExpressNumJpService {

    @Autowired
    private ExpressNumJpRepository expressNumJpRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
        expressNumJpRepository.delete(id);
    }

    @Override
    public ExpressNumJp save(ExpressNumJp expressNumJp) {
        return expressNumJpRepository.save(expressNumJp);
    }

    /**
     * 批量入库
     * @param expressNumJpList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumJp> saveInBatch(List<ExpressNumJp> expressNumJpList) {
        List<ExpressNumJp> dataList = new ArrayList<>();

        for (ExpressNumJp expressNumJp : expressNumJpList) {
            if (dataList.size() == 1000) {
                expressNumJpRepository.save(dataList);
                dataList.clear();
            }
            dataList.add(expressNumJp);
        }
        if (!dataList.isEmpty()) {
            expressNumJpRepository.save(dataList);
        }
        return expressNumJpList;
    }

    @Override
    public ExpressNumJp findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumJpRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
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
        ExpressNumJp expressNumJp = expressNumJpRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumJp;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
        ExpressNumJp expressNumJp = expressNumJpRepository.findOne(id);
        expressNumJp.setStatus(code);
        expressNumJpRepository.save(expressNumJp);
    }

    @Override
    public ExpressNumJp findOne(Integer id) {
        return expressNumJpRepository.findOne(id);
    }

    @Override
    public ExpressNumJp findByExpNum(String expNum) {
        return expressNumJpRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumJp> findAll() {
        return expressNumJpRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumJpRepository.countByStatus(status);
    }
}
