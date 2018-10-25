package com.jizhangyl.application.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.jizhangyl.application.enums.ResultEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jizhangyl.application.dataobject.primary.SysMenu;
import com.jizhangyl.application.dto.SysMenuDTO;
import com.jizhangyl.application.enums.MenuEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.SysMenuRepository;
import com.jizhangyl.application.service.SysMenuService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:22:42
 * @description SysMenuService的实现类
 */
@Slf4j
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuRepository sysMenuRepository;

    @Override
    public SysMenuDTO findById(Integer menuId) {
        SysMenuDTO result = new SysMenuDTO();
        SysMenu menu = sysMenuRepository.findOne(menuId);
        SysMenu pMenu = sysMenuRepository.findOne(menu.getParentId());
        
        BeanUtils.copyProperties(menu, result);
        if (pMenu != null) {
            result.setParentName(pMenu.getName());
        }
        return result;
    }
    
    @Override
    public List<SysMenuDTO> findAll() {
        List<SysMenu> menuList = sysMenuRepository.findAll();
        
        Map<Integer, SysMenu> menuMap = new HashMap<Integer, SysMenu>(menuList.size());
        for (SysMenu sysMenu : menuList) {
            menuMap.put(sysMenu.getId(), sysMenu);
        }
        
        List<SysMenuDTO> menuDTOList = new ArrayList<SysMenuDTO>(menuList.size()); 
        menuDTOList = menuList.stream().map(e->{
            SysMenuDTO sysMenuDTO = new SysMenuDTO();
            BeanUtils.copyProperties(e, sysMenuDTO);
            
            SysMenu parentMenu = menuMap.get(e.getParentId());
            String parentName = "";
            if (parentMenu != null) {
                parentName = parentMenu.getName();
            }
            sysMenuDTO.setParentName(parentName);
            return sysMenuDTO;
        }).collect(Collectors.toList());
        
        return menuDTOList;
    }

    @Override
    public List<SysMenu> findByIdIn(List<Integer> ids) {
        List<SysMenu> menuList = new ArrayList<SysMenu>(100);
        if (ids == null || ids.size() == 0) {
            log.info("【查找在id列表中的菜单，记录为空】");
            return menuList;
        }
        menuList = sysMenuRepository.findByIdIn(ids);
        return menuList;
    }
    
    @Override
    public void save(SysMenu sysMenu) {
        Objects.requireNonNull(sysMenu, "【新增菜单】空指针异常-sysMenu为空");
        // 按钮之外的菜单不允许重名
        if (!sysMenu.getType().equals(MenuEnum.BUTTON.getCode()) && findByName(sysMenu.getName()) != null) {
            log.info("【新增菜单】菜单名称已经存在");
            throw new GlobalException(ResultEnum.MENU_IS_EXIST.getCode(),
                String.format(ResultEnum.MENU_IS_EXIST.getMessage(), sysMenu.getName()));
        }
        if (sysMenuRepository.findByPerms(sysMenu.getPerms()) != null) {
            log.info("【新增菜单】权限标识已经存在");
            throw new GlobalException(ResultEnum.PERMS_IS_EXIST.getCode(),
                String.format(ResultEnum.PERMS_IS_EXIST.getMessage(), sysMenu.getPerms()));
        }
        SysMenu parentMenu = sysMenuRepository.findOne(sysMenu.getParentId());
        if (parentMenu == null) {
            log.info("【新增菜单】上级菜单不存在");
            throw new GlobalException(ResultEnum.UP_MENU_NOT_EXIST);
        }
        /*else if (parentMenu.getId().equals(sysMenu.getId())) { // 新增菜单的话还没有id,所以这种条件不成立
            log.info("【新增菜单】当前菜单的上级菜单不能是它本身");
            throw new GlobalException(ResultEnum.MENU_SELF);
        }*/

//        sysMenu.setUpdateBy("openid"); 后期shiro集成进来
        
        sysMenuRepository.save(sysMenu);
    }

    /**
     * $$$$$-角色菜单对应表中的数据也需要删除-$$$$$
     */
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Integer menuId) {
        /**
         * 1. 判空(done)
         * 2. 判断数据库中是否有该menuId的菜单(done)
         * 3. 判断该菜单是否有子菜单(done)
         * 4. 先删除角色-菜单对应表中与该菜单对应的记录
         * 5. 再删除菜单
         */
        Objects.requireNonNull(menuId, "【删除菜单】菜单id为空");
        SysMenu sysMenu = sysMenuRepository.findOne(menuId);
        if (sysMenu == null) {
            log.info("【删除菜单】待删除的菜单不存在");
            throw new GlobalException(ResultEnum.MENU_NOT_EXIST);
        }
        Integer count = sysMenuRepository.countByParentId(menuId);
        if (count > 0) {
            log.info("【删除菜单】当前菜单还有下级菜单,不能删除");
            throw new GlobalException(ResultEnum.DOWN_MENU_IS_EXIST);
        }
        
        sysMenuRepository.delete(menuId);
    }

    @Override
    public void update(SysMenu sysMenu) {
        Objects.requireNonNull(sysMenu, "【修改菜单】sysMen为空");
        Objects.requireNonNull(sysMenu.getId(), "【修改菜单】菜单id为空");
        
        SysMenu menu = sysMenuRepository.findOne(sysMenu.getId());
        
        // (非按钮)修改的菜单名称和它原来的不相等的情况下，不能和其他菜单重名
        if (!sysMenu.getType().equals(MenuEnum.BUTTON.getCode()) && !sysMenu.getName().equals(menu.getName())) {
            if (findByName(sysMenu.getName()) != null) {
                log.info("【修改菜单】菜单名称已经存在");
                throw new GlobalException(ResultEnum.MENU_IS_EXIST.getCode(),
                        String.format(ResultEnum.MENU_IS_EXIST.getMessage(), sysMenu.getName()));
            }
        }
        
        // 修改的权限标识和它原来的不相等的情况下,不能和其他的权限名重名
        if (!sysMenu.getPerms().equals(menu.getPerms())) {
            if (sysMenuRepository.findByPerms(sysMenu.getPerms()) != null) {
                log.info("修改菜单】权限标识已经存在");
                throw new GlobalException(ResultEnum.PERMS_IS_EXIST.getCode(),
                        String.format(ResultEnum.PERMS_IS_EXIST.getMessage(), sysMenu.getPerms()));
            }
        }
        
        SysMenu parentMenu = sysMenuRepository.findOne(sysMenu.getParentId());
        if (parentMenu == null) {
            log.info("【修改菜单】上级菜单不存在");
            throw new GlobalException(ResultEnum.UP_MENU_NOT_EXIST);
        } else if (parentMenu.getId().equals(sysMenu.getId())) {
            log.info("【新增菜单】当前菜单的上级菜单不能是它本身");
            throw new GlobalException(ResultEnum.MENU_SELF);
        }
//      sysMenu.setUpdateBy("openid"); 后期shiro集成进来
        sysMenuRepository.save(sysMenu);
    }

    @Override
    public SysMenu findByName(String name) {
        return sysMenuRepository.findByName(name);
    }
}
