package com.jizhangyl.application.dto;

import java.util.List;

import com.jizhangyl.application.dataobject.SysMenu;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月11日 下午3:01:42
 * @description 新增上级菜单名称
 */
@Data
public class SysMenuDTO extends SysMenu {
    private static final long serialVersionUID = -7452917946395281363L;
    
    private String parentName;
    
    private List<SysMenuDTO> children;
}
