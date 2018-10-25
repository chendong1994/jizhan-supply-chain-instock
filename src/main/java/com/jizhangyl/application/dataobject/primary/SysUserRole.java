package com.jizhangyl.application.dataobject.primary;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月8日 下午3:43:17
 * @description 用户与角色之间的对应关系
 */
@Data
@Entity
@DynamicUpdate
public class SysUserRole implements Serializable {
    private static final long serialVersionUID = -2042488796478317072L;

    @Id
    @GeneratedValue
    private Integer id;

    private Integer userId;

    private Integer roleId;
}
