package com.jizhangyl.application.dataobject.primary;

import com.jizhangyl.application.utils.excel.Excel;
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
public class RepositoryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productId;

    @Excel(column = "A", name = "商品名称")
    private String productName;

    @Excel(column = "B", name = "当前库存", width = 12 * 256)
    private Integer productStock;

    @Excel(column = "C", name = "在途库存", width = 12 * 256)
    private Integer wayStock;

    @Excel(column = "D", name = "昨日出库数", width = 12 * 256)
    private Integer yestOutNum;

    @Excel(column = "E", name = "近7日出库数", width = 12 * 256)
    private Integer weekOutNum;

    @Excel(column = "F", name = "待出库数", width = 12 * 256)
    private Integer todoOutNum;

    private String productImage;

    @Excel(column = "G", name = "商品Jan_code", width = 20 * 256)
    private String productJancode;

    @Excel(column = "H", name = "每箱入数", width = 12 * 256)
    private Integer boxNum;

    @Excel(column = "I", name = "打包码", width = 12 * 256)
    private String packCode;

    @Excel(column = "J", name = "添加时间", width = 25 * 256)
    private Date createTime;

    @Excel(column = "K", name = "更新时间", width = 25 * 256)
    private Date updateTime;
}
