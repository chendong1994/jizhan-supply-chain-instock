package com.jizhangyl.application.form;

import com.jizhangyl.application.validator.IsMobile;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * @author 杨贤达
 * @date 2018/8/7 17:52
 * @description
 */
@Data
public class WxuserAddrForm {

    @NotEmpty(message = "openid不能为空")
    private String openid;

    @NotEmpty(message = "收件人姓名不能为空")
    @Pattern(regexp = "^(?!.*?(先生|女士|小姐|叔叔|阿姨)$)[\\u4e00-\\u9fa5]{2,4}+$", message = "中文姓名格式不正确")
    private String receiver;

    private String receiverNickname;

    @NotEmpty(message = "手机号码不能为空")
    @IsMobile
    private String phone;

    @NotEmpty(message = "省份不能为空")
    private String province;

    @NotEmpty(message = "地市不能为空")
    private String city;

    @NotEmpty(message = "县区不能为空")
    private String area;

    @NotEmpty(message = "详细地址不能为空")
    private String detailAddr;

}
