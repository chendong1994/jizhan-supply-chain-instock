package com.jizhangyl.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.SysMenu;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午3:41:34
 * @description 访问菜单表的dao接口
 */
public interface SysMenuRepository extends JpaRepository<SysMenu, Integer> {

    SysMenu findByName(String name);
    
    List<SysMenu> findByParentId(Integer parentId);
    
    SysMenu findByPerms(String perms);
    
    Integer countByParentId(Integer parentId);
    
    List<SysMenu> findByIdIn(List<Integer> ids);
}
