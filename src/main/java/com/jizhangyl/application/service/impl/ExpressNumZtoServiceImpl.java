package com.jizhangyl.application.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.primary.ExpressNumZto;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpressNumZtoRepository;
import com.jizhangyl.application.service.ExpressNumZtoService;
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
public class ExpressNumZtoServiceImpl implements ExpressNumZtoService {

    @Autowired
    private ExpressNumZtoRepository expressNumZtoRepository;

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void delete(Integer id) {
        expressNumZtoRepository.deleteById(id);
    }

    @Override
    public ExpressNumZto save(ExpressNumZto expressNumZto) {
        return expressNumZtoRepository.save(expressNumZto);
    }

    /**
     * 批量入库
     * @param expressNumZtoList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ExpressNumZto> saveInBatch(List<ExpressNumZto> expressNumZtoList) {
        List<ExpressNumZto> dataList = new ArrayList<>();

        for (ExpressNumZto expressNumZto : expressNumZtoList) {
            if (dataList.size() == 1000) {
//                expressNumZtoRepository.save(dataList);
                expressNumZtoRepository.saveAll(dataList);
                dataList.clear();
            }
            dataList.add(expressNumZto);
        }
        if (!dataList.isEmpty()) {
//            expressNumZtoRepository.save(dataList);
            expressNumZtoRepository.saveAll(dataList);
        }
        return expressNumZtoList;
    }

    @Override
    public ExpressNumZto findUnused() {
        long time = System.currentTimeMillis() + RedisConstant.TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.GET_EXPRESS_NUM_FAIL);
        }

        Integer remainSize = expressNumZtoRepository.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
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
        ExpressNumZto expressNumZto = expressNumZtoRepository.findTop1ByStatus(ExpressNumStatusEnum.UNUSED.getCode());

        redisLock.unlock(this.getClass(), String.valueOf(time));

        return expressNumZto;
    }

    @Override
    public void updateStatus(Integer id, Integer code) {
        ExpressNumZto expressNumZto = expressNumZtoRepository.getOne(id);
        expressNumZto.setStatus(code);
        expressNumZtoRepository.save(expressNumZto);
    }

    @Override
    public ExpressNumZto findOne(Integer id) {
        return expressNumZtoRepository.getOne(id);
    }

    @Override
    public ExpressNumZto findByExpNum(String expNum) {
        return expressNumZtoRepository.findByExpNum(expNum);
    }

    @Override
    public List<ExpressNumZto> findAll() {
        return expressNumZtoRepository.findAll();
    }

    @Override
    public Integer countByStatus(Integer status) {
        return expressNumZtoRepository.countByStatus(status);
    }
}
