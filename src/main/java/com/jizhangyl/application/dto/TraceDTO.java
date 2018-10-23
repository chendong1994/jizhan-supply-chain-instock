package com.jizhangyl.application.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午3:04:28
 * @description 保存物流动态
 */
@Data
public class TraceDTO implements Serializable {
    private static final long serialVersionUID = 1013220383517273250L;

    private String acceptTime;
    
    private String acceptAddress;
    
    private String remark;
    
}
