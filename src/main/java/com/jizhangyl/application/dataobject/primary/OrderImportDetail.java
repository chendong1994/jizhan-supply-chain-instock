package com.jizhangyl.application.dataobject.primary;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@DynamicUpdate
@Entity
public class OrderImportDetail {

    @Id
    private String id;

    private String masterId;

    private String orderMasterId;
}