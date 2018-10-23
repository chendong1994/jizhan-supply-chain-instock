package com.jizhangyl.application.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jizhangyl.application.dataobject.SysRoleMenu;
import com.jizhangyl.application.repository.SysRoleMenuRepository;
import com.jizhangyl.application.service.SysRoleMenuService;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:25:32
 * @description SysRoleMenuService的实现类
 */
@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    @Autowired
    private SysRoleMenuRepository sysRoleMenuRepository;
    
    @Override
    public List<SysRoleMenu> findAll() {
        return sysRoleMenuRepository.findAll();
    }

    @Override
    public List<SysRoleMenu> findByRoleId(Integer roleId) {
        return sysRoleMenuRepository.findByRoleId(roleId);
    }

}
