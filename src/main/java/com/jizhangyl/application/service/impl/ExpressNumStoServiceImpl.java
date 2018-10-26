package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.primary.ExpressNumSto;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpressNumStoRepository;
import com.jizhangyl.application.service.ExpressNumStoService;
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
public class ExpressNumStoServiceImpl implements ExpressNumStoService {

    @Autowired
    private ExpressNumStoRepository expressNumStoRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
//        expressNumStoRepository.delete(id);
        expressNumStoRepository.deleteById(id);
    }

    @Override
    public ExpressNumSto save(ExpressNumSto expressNumSto) {
        return expressNumStoRepository.save(expressNumSto);
    }

    /**
     * 批量入库
     * @param expressNumStoList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumSto> saveInBatch(List<ExpressNumSto> expressNumStoList) {
        List<ExpressNumSto> dataList = new ArrayList<>();

        for (ExpressNumSto expressNumSto : expressNumStoList) {
            if (dataList.size() == 1000) {
//                expressNumStoRepository.save(dataList);
                expressNumStoRepository.saveAll(dataList);
                dataList.clear();
            }
            dataList.add(expressNumSto);
        }
        if (!dataList.isEmpty()) {
//            expressNumStoRepository.save(dataList);
            expressNumStoRepository.saveAll(dataList);
        }
        return expressNumStoList;
    }

    @Override
    public ExpressNumSto findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumStoRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
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
        ExpressNumSto expressNumSto = expressNumStoRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumSto;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
//        ExpressNumSto expressNumSto = expressNumStoRepository.findOne(id);
        ExpressNumSto expressNumSto = expressNumStoRepository.getOne(id);
        expressNumSto.setStatus(code);
        expressNumStoRepository.save(expressNumSto);
    }

    @Override
    public ExpressNumSto findOne(Integer id) {
//        return expressNumStoRepository.findOne(id);
        return expressNumStoRepository.getOne(id);
    }

    @Override
    public ExpressNumSto findByExpNum(String expNum) {
        return expressNumStoRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumSto> findAll() {
        return expressNumStoRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumStoRepository.countByStatus(status);
    }
}
