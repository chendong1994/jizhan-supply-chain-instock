package com.jizhangyl.application.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class PurchaseOrderPay {

    @Id
    private String payId;

    private String orderId;

    private Date payTime;

    private BigDecimal payAmount;

    /**
     * 备注
     */
    private String comment;

    private Date createTime;

    private Date updateTime;
}
