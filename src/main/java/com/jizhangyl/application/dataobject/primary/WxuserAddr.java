package com.jizhangyl.application.dataobject.primary;

import com.jizhangyl.application.enums.AddrTypeEnum;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/8/5 12:48
 * @description 发货地址
 */
@Data
@Entity
@DynamicUpdate
public class WxuserAddr {

    @Id
    @GeneratedValue
    private Integer id;

    private String openid;

    private String receiver;

    private String receiverNickname;

    private String phone;

    private String province;

    private String city;

    private String area;

    private String detailAddr;

    private String addrLabel;

    private Short isDefault = AddrTypeEnum.OTHER_ADDRESS.getCode().shortValue();

    private Date createTime;

    private Date updateTime;

    /**
     * 表示邮编
     */
    @Column(name = "ext_1")
    private String postCode;

}
