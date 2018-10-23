package com.jizhangyl.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.NotifyLog;

/**
 * @author 曲健磊
 * @date 2018年10月10日 下午5:26:04
 * @description 访问短信提醒日志表的接口
 */
public interface NotifyLogRepository extends JpaRepository<NotifyLog, Integer> {
    
    /**
     * 统计某个订单的短信提醒次数
     * @param orderId 订单id
     * @return 该订单的短信提醒次数
     */
    Integer countByOrderId(String orderId);
    
}
