package com.jizhangyl.application.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author 杨贤达
 * @date 2018/8/20 10:31
 * @description
 */
@Data
public class BrandForm {

    private Integer id;

    @NotEmpty(message = "名称不能为空")
    private String name;

    private Integer level;
}
