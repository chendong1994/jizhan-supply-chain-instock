package com.jizhangyl.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jizhangyl.application.dataobject.primary.SysRole;
import com.jizhangyl.application.dto.SysRoleDTO;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:15:12
 * @description 用于访问角色表的数据
 */
public interface SysRoleService {

    Page<SysRole> findAll(Pageable pageable);

    SysRole findByName(String name);

    SysRole findOne(Integer id);

    /**
     * 根据角色id查询该角色的详细信息,包含菜单列表
     */
    SysRoleDTO findRoleInfo(Integer roleId);

    void add(SysRole role, List<Integer> menuIds);

    void update(SysRole role, List<Integer> menuIds);

    void delete(Integer id);
}
