package com.jizhangyl.application.dataobject;

import com.jizhangyl.application.enums.OrderStatusEnum;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
public class Cart {

    @Id
    @GeneratedValue
    private Integer id;

    private String openid;

    private Integer productId;

    private String productName;

    private BigDecimal productPrice;

    private Integer productQuantity;

    private Integer productStatus = OrderStatusEnum.NEW.getCode();

    private Date createTime;

    private Date updateTime;

}
