package com.jizhangyl.application.VO;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 杨贤达
 * @date 2018/8/1 15:29
 * @description
 */
@Data
public class ProviderVo implements Serializable {

    private static final long serialVersionUID = -2990862169817183153L;

    private Integer providerId;

    private String companyName;

    public ProviderVo() {
    }

    public ProviderVo(Integer providerId, String companyName) {
        this.providerId = providerId;
        this.companyName = companyName;
    }
}
