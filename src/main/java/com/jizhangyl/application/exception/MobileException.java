package com.jizhangyl.application.exception;

import com.jizhangyl.application.enums.ResultEnum;
import lombok.Getter;

/**
 * @author 杨贤达
 * @date 2018/7/30 14:45
 * @description
 */
@Getter
public class MobileException extends RuntimeException {

    private Integer code;

    public MobileException() {
    }

    public MobileException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public MobileException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
