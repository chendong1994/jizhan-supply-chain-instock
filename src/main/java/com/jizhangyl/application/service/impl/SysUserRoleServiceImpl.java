package com.jizhangyl.application.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jizhangyl.application.dataobject.primary.SysUserRole;
import com.jizhangyl.application.repository.primary.SysUserRoleRepository;
import com.jizhangyl.application.service.SysUserRoleService;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:29:46
 * @description SysUserRoleService的实现类
 */
@Service
public class SysUserRoleServiceImpl implements SysUserRoleService {

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;
    
    @Override
    public List<SysUserRole> findAll() {
        return sysUserRoleRepository.findAll();
    }

    @Override
    public List<SysUserRole> findByRoleId(Integer roleId) {
        return sysUserRoleRepository.findByRoleId(roleId);
    }
    
    

}
