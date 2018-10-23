package com.jizhangyl.application.dto;

import com.jizhangyl.application.enums.ShopStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/8/9 14:07
 * @description
 */
@Data
public class ShopDto {

    // TODO 引入采购订单添加
    private Integer id;

    private String shopName;

    private Integer shopCount;

    private String shopImage;

    private String shopJan;

    private String cateName;

    private String brandName;

    private Integer shopXcount;

    private String shopFormat;

    private Integer isPaogoods;

    private Integer shopJweight;

    private Integer shopDweight;

    private String shopColor;

    private String shopWhg;

    private String shopVolume;

    private BigDecimal shopGprice;

    private BigDecimal shopLprice;

    private BigDecimal bcPrice;

    private Double bcCess;

    private BigDecimal bcCprice;

    private String wenan;

    private String shopStatus;

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
     * 税率
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
}
