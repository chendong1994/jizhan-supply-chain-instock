package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.primary.ExpressNumSf;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpressNumSfRepository;
import com.jizhangyl.application.service.ExpressNumSfService;
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
public class ExpressNumSfServiceImpl implements ExpressNumSfService {

    @Autowired
    private ExpressNumSfRepository expressNumSfRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
//        expressNumSfRepository.delete(id);
        expressNumSfRepository.deleteById(id);
    }

    @Override
    public ExpressNumSf save(ExpressNumSf expressNumSf) {
        return expressNumSfRepository.save(expressNumSf);
    }

    /**
     * 批量入库
     * @param expressNumSfList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumSf> saveInBatch(List<ExpressNumSf> expressNumSfList) {
        List<ExpressNumSf> dataList = new ArrayList<>();

        for (ExpressNumSf expressNumSf : expressNumSfList) {
            if (dataList.size() == 1000) {
//                expressNumSfRepository.save(dataList);
                expressNumSfRepository.saveAll(dataList);
                dataList.clear();
            }
            dataList.add(expressNumSf);
        }
        if (!dataList.isEmpty()) {
//            expressNumSfRepository.save(dataList);
            expressNumSfRepository.saveAll(dataList);
        }
        return expressNumSfList;
    }

    @Override
    public ExpressNumSf findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumSfRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
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
        ExpressNumSf expressNumSf = expressNumSfRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumSf;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
//        ExpressNumSf expressNumSf = expressNumSfRepository.findOne(id);
        ExpressNumSf expressNumSf = expressNumSfRepository.getOne(id);
        expressNumSf.setStatus(code);
        expressNumSfRepository.save(expressNumSf);
    }

    @Override
    public ExpressNumSf findOne(Integer id) {
//        return expressNumSfRepository.findOne(id);
        return expressNumSfRepository.getOne(id);
    }

    @Override
    public ExpressNumSf findByExpNum(String expNum) {
        return expressNumSfRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumSf> findAll() {
        return expressNumSfRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumSfRepository.countByStatus(status);
    }
}
