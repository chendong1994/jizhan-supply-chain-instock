package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.ExpressNumCc;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.ExpressNumCcRepository;
import com.jizhangyl.application.service.ExpressNumCcService;
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
public class ExpressNumCcServiceImpl implements ExpressNumCcService {

    @Autowired
    private ExpressNumCcRepository expressNumCcRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
        expressNumCcRepository.delete(id);
    }

    @Override
    public ExpressNumCc save(ExpressNumCc expressNumCc) {
        return expressNumCcRepository.save(expressNumCc);
    }

    /**
     * 批量入库
     * @param expressNumCcList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumCc> saveInBatch(List<ExpressNumCc> expressNumCcList) {
        List<ExpressNumCc> dataList = new ArrayList<>();

        for (ExpressNumCc expressNumCc : expressNumCcList) {
            if (dataList.size() == 1000) {
                expressNumCcRepository.save(dataList);
                dataList.clear();
            }
            dataList.add(expressNumCc);
        }
        if (!dataList.isEmpty()) {
            expressNumCcRepository.save(dataList);
        }
        return expressNumCcList;
    }

    @Override
    public ExpressNumCc findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumCcRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
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
        ExpressNumCc expressNumCc = expressNumCcRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumCc;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
        ExpressNumCc expressNumCc = expressNumCcRepository.findOne(id);
        expressNumCc.setStatus(code);
        expressNumCcRepository.save(expressNumCc);
    }

    @Override
    public ExpressNumCc findOne(Integer id) {
        return expressNumCcRepository.findOne(id);
    }

    @Override
    public ExpressNumCc findByExpNum(String expNum) {
        return expressNumCcRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumCc> findAll() {
        return expressNumCcRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumCcRepository.countByStatus(status);
    }
}
