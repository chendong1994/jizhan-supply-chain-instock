package com.jizhangyl.application.exception;

import com.jizhangyl.application.enums.ResultEnum;
import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/7/30 14:45
 * @description
 */
@Getter
public class AuthorizeException extends RuntimeException {

    private Integer code;

    public AuthorizeException() {
    }

    public AuthorizeException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public AuthorizeException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
