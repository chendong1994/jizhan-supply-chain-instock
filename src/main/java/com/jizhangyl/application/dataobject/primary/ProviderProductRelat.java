package com.jizhangyl.application.dataobject.primary;


import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商 => 商品
 */
@Data
@Entity
@DynamicUpdate
public class ProviderProductRelat {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer providerId;

    private Integer productId;

    private String productName;

    private String productJancode;

    private String productImage;

    private Integer boxQuantity;

    private BigDecimal purchasePrice;

    private Integer productStock;

    private Date createTime;

    private Date updateTime;

}
