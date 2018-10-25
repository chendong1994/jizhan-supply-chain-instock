package com.jizhangyl.application.dataobject.primary;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class RepositoryDelivery {

    @Id
    private String deliveryId;

    private String expressNumber;

    private String orderId;

    private String buyerName;

    private String buyerPhone;

    private String recipient;

    private String recipientPhone;

    private Integer recipientAddrId;

    private String buyerOpenid;

    private String deliveryNumber;

    private String voyage;

    private Date payTime;

    private Date orderTime;

    private Date createTime;

    private Date updateTime;
}
