package com.jizhangyl.application.service;

import java.util.List;

import com.jizhangyl.application.dataobject.SysUserRole;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:18:14
 * @description 用于访问用户角色对应表中的数据
 */
public interface SysUserRoleService {

    List<SysUserRole> findAll();

    List<SysUserRole> findByRoleId(Integer roleId);

}
