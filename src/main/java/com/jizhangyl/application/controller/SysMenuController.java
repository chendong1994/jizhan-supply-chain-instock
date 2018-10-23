package com.jizhangyl.application.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aliyuncs.utils.StringUtils;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.comparator.menu.MenuComparator;
import com.jizhangyl.application.dataobject.SysMenu;
import com.jizhangyl.application.dataobject.SysRoleMenu;
import com.jizhangyl.application.dto.SysMenuDTO;
import com.jizhangyl.application.enums.MenuEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.SysMenuService;
import com.jizhangyl.application.service.SysRoleMenuService;
import com.jizhangyl.application.utils.MenuUtil;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年10月9日 下午4:38:07
 * @description 处理菜单的请求
 */
@Slf4j
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;
    
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 获取当前用户拥有的菜单列表
     * @return
     */
    @GetMapping("/nav")
    public ResultVO navList() {
        // 1.获取当前登录用户的id,查出该用户拥有的菜单列表
        List<SysMenuDTO> menuList = sysMenuService.findAll();
        // 2.1.找出一级的菜单
        List<SysMenuDTO> returnList = menuList.stream()
                                              .filter(e -> e.getParentId().equals(MenuEnum.TOP_NUM.getCode()))
                                              .collect(Collectors.toList());
        // 2.2.对一级菜单进行排序
        Collections.sort(returnList, new MenuComparator());
        // 3.1.找出每个一级菜单下的二级菜单
        for (SysMenuDTO menu : returnList) {
            Integer id = menu.getId();
            List<SysMenuDTO> childrens = menuList.stream()
                                                 .filter(e -> e.getParentId().equals(id))
                                                 .collect(Collectors.toList());
            Collections.sort(childrens, new MenuComparator());
            menu.setChildren(childrens);
        }
        return ResultVOUtil.success(returnList);
    }
    
    @GetMapping("/list")
    public ResultVO list() {
        List<SysMenuDTO> menuList = sysMenuService.findAll();
        List<SysMenuDTO> returnMenuList = new ArrayList<SysMenuDTO>();
        
        MenuUtil.sortTreeList(returnMenuList, menuList, 0);
        
        return ResultVOUtil.success(returnMenuList);
    }

    @GetMapping("/getMenuInfo")
    public ResultVO getRole(@RequestParam("menuId") Integer menuId) {
        
        SysMenuDTO sysMenuDTO = sysMenuService.findById(menuId);
        
        return ResultVOUtil.success(sysMenuDTO);
    }
    
    @GetMapping("/getMenuListByRoleId")
    public ResultVO getMenuListByRoleId(@RequestParam Integer roleId) {

        List<SysRoleMenu> roleMenuList = sysRoleMenuService.findByRoleId(roleId);

        List<Integer> menuIds = roleMenuList.stream().map(e->e.getMenuId()).collect(Collectors.toList());

        return ResultVOUtil.success(menuIds);
    }

    @GetMapping("/save")
    public ResultVO save(SysMenu sysMenu) {
        if (sysMenu.getParentId() == null || sysMenu.getType() == null
            || StringUtils.isEmpty(sysMenu.getName())
            || StringUtils.isEmpty(sysMenu.getPerms())) {
            log.info("【添加菜单的基本必填信息未填写，请检菜单类型,菜单名称,上级菜单,授权标识是否为空】");
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        
        if (sysMenu.getType().equals(MenuEnum.CATALOG.getCode())) { // 目录:排序号,菜单图标不能为空
            if (sysMenu.getSort() == null || StringUtils.isEmpty(sysMenu.getIcon())) {
                log.info("【新增目录时出现空指针异常，请检查排序号,菜单图标是否为空】");
                throw new GlobalException(ResultEnum.PARAM_EMPTY);
            }
        } else if (sysMenu.getType().equals(MenuEnum.MENU.getCode())) { // 菜单:url,排序号,菜单图标不能为空
            if (sysMenu.getSort() == null
                || StringUtils.isEmpty(sysMenu.getUrl())
                || StringUtils.isEmpty(sysMenu.getIcon())) {
                log.info("【新增菜单时出现空指针异常，请检查url,排序号,菜单图标是否为空】");
                throw new GlobalException(ResultEnum.PARAM_EMPTY);
            }
        }
        sysMenuService.save(sysMenu);
        return ResultVOUtil.success();
    }

    @GetMapping("/delete")
    public ResultVO delete(@RequestParam("menuId") Integer menuId) {
        Objects.requireNonNull(menuId, "【删除菜单】待删除的菜单id为空");
        sysMenuService.delete(menuId);
        return ResultVOUtil.success();
    }

    @GetMapping("/update")
    public ResultVO update(SysMenu sysMenu) {
        if (sysMenu.getParentId() == null || sysMenu.getType() == null
            || StringUtils.isEmpty(sysMenu.getName())
            || StringUtils.isEmpty(sysMenu.getPerms())) {
            log.info("【修改菜单的基本必填信息未填写，请检菜单类型,菜单名称,上级菜单,授权标识是否为空】");
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        
        if (sysMenu.getType().equals(MenuEnum.CATALOG.getCode())) { // 目录:排序号,菜单图标不能为空
            if (sysMenu.getSort() == null || StringUtils.isEmpty(sysMenu.getIcon())) {
                log.info("【修改目录时出现空指针异常，请检查排序号,菜单图标是否为空】");
                throw new GlobalException(ResultEnum.PARAM_EMPTY);
            }
        } else if (sysMenu.getType().equals(MenuEnum.MENU.getCode())) { // 菜单:url,排序号,菜单图标不能为空
            if (sysMenu.getSort() == null
                || StringUtils.isEmpty(sysMenu.getUrl())
                || StringUtils.isEmpty(sysMenu.getIcon())) {
                log.info("【修改菜单时出现空指针异常，请检查url,排序号,菜单图标是否为空】");
                throw new GlobalException(ResultEnum.PARAM_EMPTY);
            }
        }
        sysMenuService.update(sysMenu);
        return ResultVOUtil.success();
    }
}
