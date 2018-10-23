package com.jizhangyl.application.dto;


import com.jizhangyl.application.dataobject.PurchaseOrderDetail;
import com.jizhangyl.application.enums.PayStatusEnum;
import com.jizhangyl.application.enums.PurchaseOrderStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PurchaseOrderDto {

    private String orderId;

    private Integer orderStatus = PurchaseOrderStatusEnum.NEW.getCode();

    private BigDecimal orderAmount;

    private BigDecimal realAmount;

    private Integer loanDate;

    private Integer payStatus = PayStatusEnum.WAIT.getCode();

    private Date payDate;

    private Integer providerId;

    private String providerName;

    private String operId;

    private List<PurchaseOrderDetail> purchaseOrderDetailList;

    private List<String> certUrlList;

    private BigDecimal payAmount;

    /**
     * 备注
     */
    private String comment;

    /**
     * 币种
     */
    private Integer currency;
}
