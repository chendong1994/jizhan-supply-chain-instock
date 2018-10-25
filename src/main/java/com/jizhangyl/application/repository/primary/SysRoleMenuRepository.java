package com.jizhangyl.application.repository.primary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.primary.SysRoleMenu;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午3:44:16
 * @description 访问角色-菜单对应表的dao接口
 */
public interface SysRoleMenuRepository extends JpaRepository<SysRoleMenu, Integer> {

    List<SysRoleMenu> findByRoleId(Integer roleId);
    
    void removeByRoleId(Integer roleId);
    
}
