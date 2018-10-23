package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.enums.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.jizhangyl.application.dataobject.NotifyLog;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.NotifyLogRepository;
import com.jizhangyl.application.service.NotifyLogService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年10月10日 下午5:28:41
 * @description 用于访问短信提醒日志
 */
@Slf4j
@Service
public class NotifyLogServiceImpl implements NotifyLogService {

    @Autowired
    private NotifyLogRepository notifyLogRepository;
    
    /**
     * 统计某个订单的短信提醒次数
     * @param orderId 订单id
     * @return 该订单的短信提醒次数
     */
    @Override
    public Integer countByOrderId(String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            log.info("【要统计短信提醒次数的订单id为空】");
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        
        return notifyLogRepository.countByOrderId(orderId);
    }

    /**
     * 添加一条短信提醒记录
     * @param notifyLog 短信提醒记录
     */
    @Override
    public void saveNotifyLog(NotifyLog notifyLog) {
        if (notifyLog == null || StringUtils.isEmpty(notifyLog.getOrderId())) {
            log.info("【要添加的短信提醒记录为空或者短信提醒记录中的订单id为空】");
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        
        notifyLogRepository.save(notifyLog);
    }

}
