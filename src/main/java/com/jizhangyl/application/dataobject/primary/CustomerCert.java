package com.jizhangyl.application.dataobject.primary;

import com.jizhangyl.application.enums.CertSaveStatusEnum;
import com.jizhangyl.application.enums.CheckStatusEnum;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@DynamicUpdate
public class CustomerCert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String idNum;

    private String openid;

    private Integer checkStatus = CheckStatusEnum.WAIT_CONFIRM.getCode();

    private String cardFront;

    private String cardRear;

    private String expressNum;

    private Integer saveStatus = CertSaveStatusEnum.NOT_SAVE.getCode();
}
