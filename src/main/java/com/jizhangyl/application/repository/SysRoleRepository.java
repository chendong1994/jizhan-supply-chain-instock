package com.jizhangyl.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.SysRole;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午3:43:09
 * @description 访问角色表的dao接口
 */
public interface SysRoleRepository extends JpaRepository<SysRole, Integer> {
    
    SysRole findByName(String name);
    
    List<SysRole> findByIdIn(List<Integer> roleIds);
    
}
