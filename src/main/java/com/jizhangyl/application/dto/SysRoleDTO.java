package com.jizhangyl.application.dto;

import java.util.List;

import com.jizhangyl.application.dataobject.SysMenu;
import com.jizhangyl.application.dataobject.SysRole;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月16日 下午7:57:09
 * @description 包含了角色的菜单列表
 */
@Data
public class SysRoleDTO extends SysRole {

    private static final long serialVersionUID = -8693885389057705573L;
    
    private List<SysMenu> menuList;
}
