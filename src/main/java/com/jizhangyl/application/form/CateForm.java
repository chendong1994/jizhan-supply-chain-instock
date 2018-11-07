package com.jizhangyl.application.form;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author 杨贤达
 * @date 2018/8/9 17:30
 * @description
 */
@Data
public class CateForm {

//    @NotNull(message = "类目id不能为空")
    private Integer id;

    @NotEmpty(message = "类目名称不能为空")
    private String name;
}
