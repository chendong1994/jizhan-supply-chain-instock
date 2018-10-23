package com.jizhangyl.application.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 杨贤达
 * @date 2018/8/7 17:52
 * @description
 */
@Data
public class WxuserAddrUpdateForm extends WxuserAddrForm{

    @NotNull(message = "id不能为空")
    private Integer id;

}
