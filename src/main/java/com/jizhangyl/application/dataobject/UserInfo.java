package com.jizhangyl.application.dataobject;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * @author 杨贤达
 * @date 2018/8/15 15:36
 * @description 后台扫码登录对象
 */
@Data
@Entity
@DynamicUpdate
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -2849718899727500151L;

    @Id
    @GeneratedValue
    private Integer id;

    private String empNo; // 员工号

    private String deptNo; // 部门号

    private String username;

    private String head;

    /**
     * 微信用户 openid
     */
    private String openid;

    /**
     * 微信用户的unionid
     */
    private String unionid;
    
    /**
     * 1-员工, 2-代购
     */
    private Integer role;

    private Date registerDate;

    private Date lastLoginDate;

    private Integer loginCount;

    private Date createTime;

    private Date updateTime;

    private Integer frozen; // 是否冻结，0 - 未冻结，1 - 已冻结
}
