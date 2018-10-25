package com.jizhangyl.application.dataobject.primary;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class ConfirmCert {

    @Id
    @GeneratedValue
    private Integer id;

    private String confirmId;

    private String confirmCert;

    private Date createTime;

    private Date updateTime;

}