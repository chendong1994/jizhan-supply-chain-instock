package com.jizhangyl.application.service;

import java.util.List;

import com.jizhangyl.application.dataobject.SysRoleMenu;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:16:25
 * @description 用于访问角色菜单对应表中的数据
 */
public interface SysRoleMenuService {
    
    List<SysRoleMenu> findAll();
    
    List<SysRoleMenu> findByRoleId(Integer roleId);
    
}
