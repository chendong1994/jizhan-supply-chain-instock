package com.jizhangyl.application.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.utils.serializer.CustomDateSerializer;
import lombok.Data;

import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/10/9 11:52
 * @description
 */
@Data
public class ConfirmCertVO {

    private String confirmCert;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    public ConfirmCertVO(String confirmCert, Date createTime) {
        this.confirmCert = confirmCert;
        this.createTime = createTime;
    }
}
