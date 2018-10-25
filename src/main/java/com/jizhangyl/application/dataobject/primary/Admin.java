package com.jizhangyl.application.dataobject.primary;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@DynamicUpdate
public class Admin {

  @Id
  @GeneratedValue
  private Integer id;

  private String name;

  private String password;

  private Integer level;
}
