package com.jizhangyl.application.form;

import com.jizhangyl.application.validator.IsMobile;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

/**
 * @author 杨贤达
 * @date 2018/8/7 18:02
 * @description
 */
@Data
public class WxuserSenderForm {

    @NotEmpty(message = "openid不能为空")
    private String openid;

    @NotEmpty(message = "发件人不能为空")
    private String sender;

    @NotEmpty(message = "手机号码不能为空")
    @IsMobile
    private String phone;
}
