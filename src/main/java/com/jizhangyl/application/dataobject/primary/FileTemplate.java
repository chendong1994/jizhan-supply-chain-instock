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
public class FileTemplate {

    @Id
    @GeneratedValue
    private Integer templateId;

    private Integer templateNo;

    private String templateName;

    private String templateUrl;

    private Date createTime;

    private Date updateTime;
}
