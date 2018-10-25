package com.jizhangyl.application.dataobject.primary;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
@DynamicUpdate
public class PurchasePayDetail {

    @Id
    private String detailId;

    private String payId;

    private Integer productId;

    private String productName;

    private String productJancode;

    private BigDecimal productPrice;

    private Integer productQuantity;
}
