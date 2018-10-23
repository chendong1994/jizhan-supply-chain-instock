package com.jizhangyl.application.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.utils.serializer.CustomDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/8/18 16:19
 * @description
 */
@Data
public class ProviderProductRelatVo implements Serializable {

    private static final long serialVersionUID = 4054072396886515668L;

    private Integer id;

    private Integer providerId;

    private String providerName;

    private Integer productId;

    private String productName;

    private String productJancode;

    private String productImage;

    private Integer boxQuantity;

    private BigDecimal purchasePrice;

    private Integer productStock;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

}
