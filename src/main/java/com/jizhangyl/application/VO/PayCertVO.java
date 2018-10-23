package com.jizhangyl.application.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.utils.serializer.CustomDateSerializer;
import lombok.Data;

import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/10/9 11:56
 * @description
 */
@Data
public class PayCertVO {

    private String payCert;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    public PayCertVO(String payCert, Date createTime) {
        this.payCert = payCert;
        this.createTime = createTime;
    }
}
