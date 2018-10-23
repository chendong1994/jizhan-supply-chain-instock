package com.jizhangyl.application.dataobject;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.PayStatusEnum;
import com.jizhangyl.application.enums.PurchaseOrderStatusEnum;
import com.jizhangyl.application.utils.EnumUtil;
import com.jizhangyl.application.utils.serializer.CustomDateSerializer;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class PurchaseOrderMaster {

    @Id
    private String orderId;

    private Integer orderStatus = PurchaseOrderStatusEnum.NEW.getCode();

    private BigDecimal orderAmount;

    private BigDecimal realAmount;

    private Integer loanDate;

    private Integer payStatus = PayStatusEnum.WAIT.getCode();

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date payDate;

    private Integer providerId;

    private String providerName;

    private String operId;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    @JsonIgnore
    public PurchaseOrderStatusEnum getOrderStatusEnum() {
        return EnumUtil.getByCode(orderStatus, PurchaseOrderStatusEnum.class);
    }
}
