package com.jizhangyl.application.dto;

import java.util.List;

import com.jizhangyl.application.dataobject.SysMenu;
import com.jizhangyl.application.dataobject.SysRole;
import com.jizhangyl.application.dataobject.UserInfo;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月16日 下午5:50:45
 * @description 包含了角色列表,菜单列表
 */
@Data
public class UserInfoDTO extends UserInfo {
    private static final long serialVersionUID = -63821945354176278L;

    private List<SysRole> roleList;

    private List<SysMenu> menuList;
}
