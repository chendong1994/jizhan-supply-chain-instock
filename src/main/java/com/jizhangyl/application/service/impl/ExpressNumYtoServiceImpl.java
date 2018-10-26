package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.primary.ExpressNumYto;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpressNumYtoRepository;
import com.jizhangyl.application.service.ExpressNumYtoService;
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
public class ExpressNumYtoServiceImpl implements ExpressNumYtoService {

    @Autowired
    private ExpressNumYtoRepository expressNumYtoRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
//        expressNumYtoRepository.delete(id);
        expressNumYtoRepository.deleteById(id);
    }

    @Override
    public ExpressNumYto save(ExpressNumYto expressNumYto) {
        return expressNumYtoRepository.save(expressNumYto);
    }

    /**
     * 批量入库
     * @param expressNumYtoList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumYto> saveInBatch(List<ExpressNumYto> expressNumYtoList) {
        List<ExpressNumYto> dataList = new ArrayList<>();

        for (ExpressNumYto expressNumYto : expressNumYtoList) {
            if (dataList.size() == 1000) {
//                expressNumYtoRepository.save(dataList);
                expressNumYtoRepository.saveAll(dataList);
                dataList.clear();
            }
            dataList.add(expressNumYto);
        }
        if (!dataList.isEmpty()) {
            expressNumYtoRepository.saveAll(dataList);
        }
        return expressNumYtoList;
    }

    @Override
    public ExpressNumYto findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumYtoRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
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
        ExpressNumYto expressNumYto = expressNumYtoRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumYto;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
        ExpressNumYto expressNumYto = expressNumYtoRepository.getOne(id);
        expressNumYto.setStatus(code);
        expressNumYtoRepository.save(expressNumYto);
    }

    @Override
    public ExpressNumYto findOne(Integer id) {
        return expressNumYtoRepository.getOne(id);
    }

    @Override
    public ExpressNumYto findByExpNum(String expNum) {
        return expressNumYtoRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumYto> findAll() {
        return expressNumYtoRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumYtoRepository.countByStatus(status);
    }
}
