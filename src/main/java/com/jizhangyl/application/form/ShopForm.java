package com.jizhangyl.application.form;

import com.jizhangyl.application.validator.IsPaoGoods;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author 杨贤达
 * @date 2018/8/19 18:25
 * @description
 */
@Data
public class ShopForm {

    private Integer id;

    @NotEmpty(message = "商品名称不能为空")
    private String shopName;

    @NotNull(message = "商品库存不能为空")
    private Integer shopCount;

    //    @NotEmpty(message = "商品图片不能为空")
    private String shopImage;

    @NotEmpty(message = "商品Jancode不能为空")
    private String shopJan;

    @NotNull(message = "类目不能为空")
    private Integer cateId;

    @NotNull(message = "品牌不能为空")
    private Integer brandId;

    @NotNull(message = "每箱入数不能为空")
    private Integer shopXcount;

    @NotEmpty(message = "商品规格不能为空")
    private String shopFormat;

    @NotNull(message = "是否抛货不能为null")
    @IsPaoGoods
//    @Pattern(regexp = "[01]]", message = "isPaogoods取值: 0或1")
    private Integer isPaogoods;

    @NotNull(message = "净重不能为空")
    private Integer shopJweight;

    @NotNull(message = "打包重量不能为空")
    private Integer shopDweight;

    @NotEmpty(message = "商品颜色不能为空")
    private String shopColor;

    @NotEmpty(message = "长宽高不能为空")
    private String shopWhg;

    @NotEmpty(message = "商品体积不能为空")
    private String shopVolume;

    @NotNull(message = "供货价不能为空")
    private BigDecimal shopGprice;

    @NotNull(message = "零售价不能为空")
    private BigDecimal shopLprice;

    private String wenan;

    /**
     * 仓库打包识别码
     */
    @NotEmpty(message = "仓库打包识别码不能为空")
    private String packCode;

    /**
     * 货物渠道
     */
    private Integer productChannel;

    /**
     * 是否含有中文标签
     */
    private Integer cnLabel;
}
