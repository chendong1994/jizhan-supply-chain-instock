package com.jizhangyl.application.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/8/9 14:07
 * @description
 */
@Data
public class ShopDto {

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

    private String wenan;

    private String shopStatus;

    /**
     * 仓库打包识别码
     */
    private String packCode;

    /**
     * 货物渠道
     */
    private Integer productChannel;

    /**
     * 是否含有中文标签
     */
    private Integer CnLabel;
}
