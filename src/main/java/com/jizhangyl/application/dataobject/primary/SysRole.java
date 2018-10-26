package com.jizhangyl.application.dataobject.primary;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月8日 下午3:38:00
 * @description 角色实体类
 */
@Data
@Entity
@DynamicUpdate
public class SysRole implements Serializable {
    private static final long serialVersionUID = 7624678146564929028L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String remark;

    private Date createTime;

    private Date updateTime;

    private String updateBy = "oYAqI1BOwVet_Cjl6a-6lqhJUzlY"; // 对应wxuser的openid
}
