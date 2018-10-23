package com.jizhangyl.application.deprecated;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.PayStatusEnum;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class PurchaseOrderMaster2 {

  @Id
  private String id;

  private String userId;

  private BigDecimal orderAmount;

  private BigDecimal realAmount;

  private Integer orderStatus = OrderStatusEnum.NEW.getCode();

  private Integer payStatus = PayStatusEnum.WAIT.getCode();

  private Date payDate;
}
