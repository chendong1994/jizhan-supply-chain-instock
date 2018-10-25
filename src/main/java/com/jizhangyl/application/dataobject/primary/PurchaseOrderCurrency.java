package com.jizhangyl.application.dataobject.primary;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class PurchaseOrderCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderId;

    private Integer currency;

    public PurchaseOrderCurrency() {
    }

    public PurchaseOrderCurrency(String orderId, Integer currency) {
        this.orderId = orderId;
        this.currency = currency;
    }
}
