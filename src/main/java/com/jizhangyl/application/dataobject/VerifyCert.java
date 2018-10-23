package com.jizhangyl.application.dataobject;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.utils.serializer.CustomDateSerializer;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class VerifyCert {

    @Id
    @GeneratedValue
    private Integer id;

    private String orderId;

    private String certUrl;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;
}
