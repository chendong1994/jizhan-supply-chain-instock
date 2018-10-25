package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.NotifyLog;

/**
 * @author 曲健磊
 * @date 2018年10月10日 下午5:28:00
 * @description 访问短信提醒日志的接口
 */
public interface NotifyLogService {
    
    /**
     * 统计某个订单的短信提醒次数
     * @param orderId 订单id
     * @return 该订单的短信提醒次数
     */
    Integer countByOrderId(String orderId);
    
    /**
     * 添加一条短信提醒记录
     * @param notifyLog 短信提醒记录
     */
    void saveNotifyLog(NotifyLog notifyLog);
    
}
