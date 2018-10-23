package com.jizhangyl.application.form;

import com.jizhangyl.application.enums.CertSaveStatusEnum;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author 杨贤达
 * @date 2018/8/25 20:37
 * @description
 */
@Data
public class UploadCertForm {

//    @NotEmpty(message = "recipient不能为空")
//    private String recipient;

    @NotEmpty(message = "expressNum不能为空")
    private String expressNum;

    @NotEmpty(message = "openid不能为空")
    private String openid;

    @NotEmpty(message = "姓名不能为空")
    private String name;

    /**
     * 身份证号码非空正则校验
     */
    @NotEmpty(message = "身份证号码不能为空")
    @Pattern(regexp = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
            "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)", message = "身份证号不正确")
    private String idNum;

//    @NotEmpty(message = "身份证正面图不能为空")
    private String cardFrontUrl;

//    @NotEmpty(message = "身份证反面图不能为空")
    private String cardRearUrl;

    @NotNull(message = "是否保存不能为空")
    private Integer saveStatus;
}
