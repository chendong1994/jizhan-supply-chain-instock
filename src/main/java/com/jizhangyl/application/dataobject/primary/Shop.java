package com.jizhangyl.application.dataobject.primary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jizhangyl.application.enums.CnLabelEnum;
import com.jizhangyl.application.enums.ProductChannelEnum;
import com.jizhangyl.application.enums.ShopStatusEnum;
import com.jizhangyl.application.utils.EnumUtil;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Integer shopStatus = ShopStatusEnum.DOWN.getCode();

    /**
     * 仓库打包识别码
     */
    private String packCode;

    /**
     * 货物渠道
     */
    private Integer productChannel = ProductChannelEnum.DEFAULT.getCode();

    /**
     * 是否含有中文标签
     */
    private Integer cnLabel = CnLabelEnum.DEFAULT.getCode();

    /**
     * 商品文案
     */
    private String wenan;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @JsonIgnore
    public ShopStatusEnum getShopStatusEnum() {
        return EnumUtil.getByCode(shopStatus, ShopStatusEnum.class);
    }
}
