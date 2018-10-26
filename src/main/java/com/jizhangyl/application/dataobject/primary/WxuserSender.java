package com.jizhangyl.application.dataobject.primary;

import com.jizhangyl.application.enums.SenderStatusEnum;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class WxuserSender {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String openid;

  private String sender;

  private String phone;

  private Integer isDefault = SenderStatusEnum.NOT_DEFAULT.getCode();

  private Date createTime;

  private Date updateTime;

}
