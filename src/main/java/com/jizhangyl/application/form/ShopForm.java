package com.jizhangyl.application.form;

import com.jizhangyl.application.validator.IsPaoGoods;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;

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

    @NotNull(message = "报关价不能为空")
    private BigDecimal bcPrice;

    @NotNull(message = "报关税率不能为空")
    private Double bcCess;

    private BigDecimal bcCprice;

    private String wenan;

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
    @NotEmpty(message = "仓库打包识别码不能为空")
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
