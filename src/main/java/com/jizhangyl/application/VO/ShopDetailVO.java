package com.jizhangyl.application.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.utils.serializer.CustomPriceSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:12
 * @description
 */
@Data
public class ShopDetailVO implements Serializable {

    private static final long serialVersionUID = -6800454685151040137L;

    private Integer id;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("shop_jan")
    private String shopJan;

    @JsonProperty("shop_image")
    private String shopImage;

    @JsonProperty("shop_count")
    private Integer shopCount;

    @JsonSerialize(using = CustomPriceSerializer.class)
    @JsonProperty("shop_gprice")
    private BigDecimal shopGprice;

    @JsonIgnore
    private Integer brandId;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("shop_jweight")
    private Integer shopJweight;

    @JsonProperty("shop_dweight")
    private Integer shopDweight;

    @JsonIgnore
    private Integer cateId;

    @JsonProperty("cate_name")
    private String cateName;

    @JsonProperty("shop_xcount")
    private Integer shopXcount;

    @JsonProperty("shop_format")
    private String shopFormat;

    @JsonProperty("is_paogoods")
    private Integer isPaogoods;

    @JsonProperty("shop_color")
    private String shopColor;

    @JsonProperty("shop_whg")
    private String shopWhg;

    @JsonProperty("shop_volume")
    private String shopVolume;

    @JsonSerialize(using = CustomPriceSerializer.class)
    @JsonProperty("bc_price")
    private BigDecimal bcPrice;

    @JsonSerialize(using = CustomPriceSerializer.class)
    @JsonProperty("shop_lprice")
    private BigDecimal shopLprice;

    @JsonProperty("bc_cess")
    private Double bcCess;

    @JsonSerialize(using = CustomPriceSerializer.class)
    @JsonProperty("bc_cprice")
    private BigDecimal bcCprice;

    private String wenan;

    public ShopDetailVO addPropName(String brandName, String cateName) {
        this.brandName = brandName;
        this.cateName = cateName;
        return this;
    }

    @JsonProperty("shop_status")
    private Integer shopStatus;

    /**
     * 海关商品类目编号
     */
    @JsonProperty("customs_cate_type")
    private String customsCateType;

    /**
     * 海关税则号列
     */
    @JsonProperty("customs_tariff_line")
    private String customsTariffLine;

    /**
     * 海关商品唯一码
     */
    @JsonProperty("customs_product_id")
    private String customsProductId;

    /**
     * 仓库打包识别码
     */
    @JsonProperty("pack_code")
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
