package com.jizhangyl.application.dataobject;


import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class PayCert {

    @Id
    @GeneratedValue
    private Integer id;

    private String payId;

    private String payCert;

    private Date createTime;

    private Date updateTime;

}
