package com.jizhangyl.application.validator;

import com.jizhangyl.application.utils.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author 杨贤达
 * @date 2018/8/19 20:33
 * @description
 */
public class IsPaoGoodsValidator implements ConstraintValidator<IsPaoGoods, Integer> {

    private boolean required = false;

    @Override
    public void initialize(IsPaoGoods constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (required) {
            return ValidatorUtil.isPaoGoods(value);
        } else {
            if (value == null) {
                return true;
            } else {
                return ValidatorUtil.isPaoGoods(value);
            }
        }
    }
}
