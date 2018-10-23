package com.jizhangyl.application.exception;import com.jizhangyl.application.enums.ResultEnum;import lombok.Getter;/** * @author 杨贤达 * @date 2018/7/27 17:24 * @description */@Getterpublic class GlobalException extends RuntimeException {    private Integer code;    public GlobalException() {    }    public GlobalException(ResultEnum resultEnum) {        super(resultEnum.getMessage());        this.code = resultEnum.getCode();    }    public GlobalException(Integer code, String msg) {        super(msg);        this.code = code;    }}