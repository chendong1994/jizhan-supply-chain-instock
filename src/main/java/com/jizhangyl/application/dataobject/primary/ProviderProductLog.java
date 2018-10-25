package com.jizhangyl.application.dataobject.primary;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月19日 上午11:17:36
 * @description 供应商=>商品历史采购价+历史库存
 */
@Data
@Entity
@DynamicUpdate
public class ProviderProductLog {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer providerId;

    private Integer productId;

    private BigDecimal purchasePrice;

    private Integer productStock;

    private Date createTime;

    private Date updateTime;

}
