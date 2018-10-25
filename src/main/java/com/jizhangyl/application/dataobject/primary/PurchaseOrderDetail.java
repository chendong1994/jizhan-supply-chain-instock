package com.jizhangyl.application.dataobject.primary;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class PurchaseOrderDetail {

    @Id
    private String detailId;

    private String orderId;

    private Integer productId;

    private String productName;

    private String productJancode;

    private BigDecimal productPrice;

    private Integer productQuantity;

    private String productImage;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;
}