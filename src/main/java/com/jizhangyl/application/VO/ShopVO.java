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
public class ShopVO implements Serializable {

    private static final long serialVersionUID = -7466277015577033167L;

    private Integer id;

    @JsonProperty("shop_jancode")
    private String shopJan;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("shop_count")
    private Integer shopCount;

    @JsonProperty("shop_image")
    private String shopImage;

    @JsonSerialize(using = CustomPriceSerializer.class)
    @JsonProperty("shop_gprice")
    private BigDecimal shopGprice;

    @JsonProperty("shop_lprice")
    private BigDecimal shopLprice; // 零售价

    @JsonProperty("shop_jweight")
    private Integer shopJweight;

    @JsonProperty("shop_dweight")
    private Integer shopDweight;

    @JsonIgnore
    private Integer brandId;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonIgnore
    private Integer cateId;

    @JsonProperty("cate_name")
    private String cateName;
    
    @JsonProperty("shop_status")
    private Integer shopStatus;

    @JsonProperty("yest_sales_num")
    private BigDecimal yestSalesNum; // 该商品昨日销量

    @JsonProperty("last_week_sales_num")
    private BigDecimal lastWeekSalesNum; // 过去7天的平均销量

    @JsonProperty("last_month_sales_num")
    private BigDecimal lastMonthSalesNum; // 过去30天的平均销量

    /**
     * 货物渠道
     */
    @JsonProperty("product_channel")
    private Integer productChannel;

    /**
     * 是否含有中文标签
     */
    @JsonProperty("cn_label")
    private Integer cnLabel;

    public ShopVO addPropName(String brandName, String cateName) {
        this.brandName = brandName;
        this.cateName = cateName;
        return this;
    }
}
