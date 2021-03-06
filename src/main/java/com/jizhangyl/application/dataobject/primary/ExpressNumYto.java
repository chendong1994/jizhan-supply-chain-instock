package com.jizhangyl.application.dataobject.primary;

import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.utils.excel.Excel;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@DynamicUpdate
public class ExpressNumYto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Excel(name = "物流单号", column = "A")
    private String expNum;

    private Integer status = ExpressNumStatusEnum.UNUSED.getCode();
}
