package com.jizhangyl.application.service;

import java.util.List;

import com.jizhangyl.application.dataobject.SysMenu;
import com.jizhangyl.application.dto.SysMenuDTO;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:13:52
 * @description 用于访问菜单表的数据
 */
public interface SysMenuService {

    SysMenuDTO findById(Integer menuId);

    List<SysMenuDTO> findAll();
    
    List<SysMenu> findByIdIn(List<Integer> ids);
    
    SysMenu findByName(String name);

    void save(SysMenu sysMenu);

    void delete(Integer menuId);

    void update(SysMenu sysMenu);
}
