package com.jizhangyl.application.dataobject.primary;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月10日 下午5:22:53
 * @description 短信提醒日志
 */
@Data
@Entity
@DynamicUpdate
public class NotifyLog {

    @Id
    @GeneratedValue
    private Integer notifyId;

    private String orderId;

    private String expressNumber;

    private String recipientPhone;

    private Date notifyTime;
}
