package com.jizhangyl.application.dataobject;

import com.jizhangyl.application.utils.excel.Excel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author 杨贤达
 * @date 2018/10/17 20:05
 * @description
 */
@Data
@Entity
public class CcTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer templateId;

    @Excel(name = "税号", column = "A")
    private String taxNum;

    @Excel(name = "品名及规格", column = "B")
    private String nameAndFormat;

    @Excel(name = "单位", column = "C")
    private String unit;

    @Excel(name = "完税价格（人民币：元）", column = "D")
    private String dutyPaying;

    @Excel(name = "税率", column = "E")
    private String taxRate;

    @Excel(name = "一级类目", column = "F")
    private String primaryCategory;

    @Excel(name = "二级类目", column = "G")
    private String secondaryCategory;

    @Excel(name = "三级类目", column = "H")
    private String thirdCategory;

    @Excel(name = "四级类目", column = "I")
    private String fourthCategory;

    @Excel(name = "五级类目", column = "J")
    private String fifthCategory;
}
