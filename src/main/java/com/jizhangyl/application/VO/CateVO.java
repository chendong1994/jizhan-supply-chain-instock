package com.jizhangyl.application.VO;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 杨贤达
 * @date 2018/8/1 17:24
 * @description
 */
@Data
public class CateVO implements Serializable {

    private static final long serialVersionUID = -5281706083543699064L;

    private Integer id;

    private String name;

    public CateVO() {
    }

    public CateVO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
