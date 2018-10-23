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
public class Brand {

  @Id
  @GeneratedValue
  private Integer id;

  private String name;

  private Integer level;

  private Date createTime;

  private Date updateTime;

}