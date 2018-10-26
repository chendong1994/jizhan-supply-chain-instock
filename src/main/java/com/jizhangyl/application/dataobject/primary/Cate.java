package com.jizhangyl.application.dataobject.primary;


import com.jizhangyl.application.enums.CateStatusEnum;
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
public class Cate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private Integer flag = CateStatusEnum.UP.getCode();

  private Date createTime;

  private Date updateTime;

}
