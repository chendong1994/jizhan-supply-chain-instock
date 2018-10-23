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
    @JsonProperty("shop_lprice")
    private BigDecimal shopLprice;

    private String wenan;

    public ShopDetailVO addPropName(String brandName, String cateName) {
        this.brandName = brandName;
        this.cateName = cateName;
        return this;
    }

    @JsonProperty("shop_status")
    private Integer shopStatus;

    /**
     * 仓库打包识别码
     */
    @JsonProperty("pack_code")
    private String packCode;

    /**
     * 货物渠道
     */
    @JsonProperty("product_channel")
    private Integer productChannel;

    /**
     * 是否含有中文标签
     */
    @JsonProperty("cn_label")
    private Integer CnLabel;
}
