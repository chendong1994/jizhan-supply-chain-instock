package com.jizhangyl.application.VO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author 杨贤达
 * @date 2018/7/27 10:06
 * @description http 请求返回的最外层对象
 */
@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = -4981563260382473972L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 具体内容
     */
    private T data;
}
