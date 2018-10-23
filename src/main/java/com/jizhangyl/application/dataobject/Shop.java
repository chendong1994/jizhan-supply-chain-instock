package com.jizhangyl.application.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jizhangyl.application.enums.ShopStatusEnum;
import com.jizhangyl.application.utils.EnumUtil;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class Shop {

    @Id
    @GeneratedValue
    private Integer id;

    private String shopName;

    private Integer shopCount;

    private String shopImage;

    private String shopJan;

    private Integer cateId;

    private Integer brandId;

    private Integer shopXcount;

    private String shopFormat;

    private Integer isPaogoods;

    private Integer shopJweight;

    private Integer shopDweight;

    private String shopColor;

    private String shopWhg;

    private String shopVolume;

    private BigDecimal shopGprice; // 供货价

    private BigDecimal shopLprice; // 零售价

    private BigDecimal bcPrice;

    private Double bcCess;

    private BigDecimal bcCprice;

    private String wenan;

    private Date createTime;

    private Date updateTime;

    private Integer shopStatus = ShopStatusEnum.DOWN.getCode();

    /**
     * 海关商品类目编号
     */
    private String customsCateType;

    /**
     * 海关税则号列
     */
    private String customsTariffLine;

    /**
     * 海关商品唯一码
     */
    private String customsProductId;

    /**
     * 仓库打包识别码
     */
    private String packCode;

    /**
     * cc税号
     */
    private String taxNum;

    /**
     * cc报关价
     */
    private String ccDeclarePrice;

    /**
     * 重量单位
     */
    private String weightUnit;

    /**
     * 完税价格
     */
    private String dutyPaying;

    /**
     * cc税率
     */
    private String taxRate;

    /**
     * 一级类目
     */
    private String primaryCategory;

    /**
     * 二级类目
     */
    private String secondaryCategory;

    /**
     * 三级类目
     */
    private String thirdCategory;

    /**
     * 四级类目
     */
    private String fourthCategory;

    /**
     * 五级类目
     */
    private String fifthCategory;

    @JsonIgnore
    public ShopStatusEnum getShopStatusEnum() {
        return EnumUtil.getByCode(shopStatus, ShopStatusEnum.class);
    }
}
