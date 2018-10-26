package com.jizhangyl.application.dataobject.primary;


import com.jizhangyl.application.enums.ExpressStatusEnum;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class ExpressQueryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String expNum;

    private Date queryDate;

    private String openid;

    private Integer expStatus = ExpressStatusEnum.FINISHED.getCode();

    private Date createTime;

    private Date updateTime;
}
