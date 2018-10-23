package com.jizhangyl.application.dataobject;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Data
@Entity
@DynamicUpdate
public class FlightPackag {
	
	@Id
    @GeneratedValue
    private Integer flightPackageId;

    private String deliveryNumber;

    private String expNum;

    private Date createTime;

    private Date updateTime;

    
}