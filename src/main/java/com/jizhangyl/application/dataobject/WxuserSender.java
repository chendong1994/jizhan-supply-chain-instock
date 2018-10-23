package com.jizhangyl.application.dataobject;

import com.jizhangyl.application.enums.SenderStatusEnum;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class WxuserSender {

  @Id
  @GeneratedValue
  private Integer id;

  private String openid;

  private String sender;

  private String phone;

  private Integer isDefault = SenderStatusEnum.NOT_DEFAULT.getCode();

  private Date createTime;

  private Date updateTime;

}
