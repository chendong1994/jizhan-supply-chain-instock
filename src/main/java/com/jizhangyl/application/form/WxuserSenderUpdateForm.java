package com.jizhangyl.application.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 杨贤达
 * @date 2018/8/7 18:02
 * @description
 */
@Data
public class WxuserSenderUpdateForm extends WxuserSenderForm{

    @NotNull(message = "id不能为空")
    private Integer id;
}
