package com.jizhangyl.application.validator;

import org.apache.poi.ss.formula.functions.T;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 杨贤达
 * @date 2018/8/19 20:26
 * @description
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { IsPaoGoodsValidator.class })
public @interface IsPaoGoods {

    boolean required() default true;

    String message() default "是否抛货值只能为0,1";

    Class<T>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
