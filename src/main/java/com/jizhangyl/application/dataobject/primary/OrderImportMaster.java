package com.jizhangyl.application.dataobject.primary;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@DynamicUpdate
@Entity
public class OrderImportMaster {

    @Id
    private String id;

    private Date importDate;

    private String fileName;

    private String buyerOpenid;

}
