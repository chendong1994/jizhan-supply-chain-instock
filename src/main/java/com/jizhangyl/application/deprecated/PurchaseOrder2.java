package com.jizhangyl.application.deprecated;

import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.PayStatusEnum;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
public class PurchaseOrder2 {

  @Id
  private String orderId;

  private String masterId;

  private Integer orderStatus = OrderStatusEnum.NEW.getCode();

  private BigDecimal orderAmount;

  private BigDecimal realAmount;

  private Integer loanDate;

  private Integer payStatus = PayStatusEnum.WAIT.getCode();

  private Date payDate;

  private Integer providerId;

  private Date createTime;

  private Date updateTime;

}
