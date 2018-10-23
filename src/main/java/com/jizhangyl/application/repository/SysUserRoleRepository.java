package com.jizhangyl.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.SysUserRole;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午3:45:17
 * @description 访问用户角色对应表的dao接口
 */
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, Integer> {

    List<SysUserRole> findByRoleId(Integer roleId);

    List<SysUserRole> findByUserId(Integer userId);

    void removeByUserId(Integer userId);
}
