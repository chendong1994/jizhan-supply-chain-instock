package com.jizhangyl.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jizhangyl.application.dataobject.SysMenu;
import com.jizhangyl.application.dataobject.SysRole;
import com.jizhangyl.application.dataobject.SysRoleMenu;
import com.jizhangyl.application.dto.SysRoleDTO;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.SysMenuRepository;
import com.jizhangyl.application.repository.SysRoleMenuRepository;
import com.jizhangyl.application.repository.SysRoleRepository;
import com.jizhangyl.application.service.SysRoleService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:24:06
 * @description SysRoleService的实现类
 */
@Slf4j
@Service
public class SysRoleServiceImpl implements SysRoleService {
    
    @Autowired
    private SysMenuRepository sysMenuRepository;

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysRoleMenuRepository sysRoleMenuRepository;

    @Override
    public Page<SysRole> findAll(Pageable pageable) {
        Page<SysRole> pageList = sysRoleRepository.findAll(pageable); 
        return pageList;
    }

    @Override
    public SysRole findByName(String name) {
        return sysRoleRepository.findByName(name);
    }

    @Override
    public SysRole findOne(Integer id) {
        return sysRoleRepository.findOne(id);
    }
    
    /**
     * 根据角色id查询该角色的详细信息,包含菜单列表
     */
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SysRoleDTO findRoleInfo(Integer roleId) {
        SysRoleDTO sysRoleDTO = new SysRoleDTO();
        SysRole role = findOne(roleId);
        if (role == null) {
            log.info("【查询角色信息】不存在roleId为" + roleId + "的角色");
            throw new GlobalException(ResultEnum.ROLE_NOT_EXIST);
        }
        BeanUtils.copyProperties(role, sysRoleDTO);
        List<SysRoleMenu> roleMenus = sysRoleMenuRepository.findByRoleId(roleId);
        List<Integer> menuIds = roleMenus.stream().map(e -> e.getMenuId()).collect(Collectors.toList());
        List<SysMenu> menuList = sysMenuRepository.findByIdIn(menuIds);
        sysRoleDTO.setMenuList(menuList);
        return sysRoleDTO;
    }
    
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void add(SysRole role, List<Integer> menuIds) {
        if (role == null || role.getName() == null) {
            log.info("【新增角色】角色名为空");
           throw new GlobalException(ResultEnum.PARAM_EMPTY); 
        }

        if (findByName(role.getName()) != null) {
            log.info("【新增角色】要添加的角色名称已经存在");
            throw new GlobalException(ResultEnum.ROLE_IS_EXIST.getCode(), String.format(ResultEnum.ROLE_IS_EXIST.getMessage(), role.getName()));
        }

        sysRoleRepository.save(role);

        if (menuIds != null && menuIds.size() != 0) {
            for (Integer menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                sysRoleMenuRepository.save(roleMenu);
            }
        }
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void update(SysRole role, List<Integer> menuIds) {
        if (role == null || role.getName() == null) {
            log.info("【修改角色】角色名为空");
           throw new GlobalException(ResultEnum.PARAM_EMPTY); 
        }

        sysRoleRepository.save(role);

        sysRoleMenuRepository.removeByRoleId(role.getId());
        
        if (menuIds != null && menuIds.size() != 0) {
            for (Integer menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                sysRoleMenuRepository.save(roleMenu);
            }
        }
    }
    
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Integer id) {
        // remove role
        sysRoleRepository.delete(id);
        // remove roleMenu
        sysRoleMenuRepository.removeByRoleId(id);
    }
}
