package com.jizhangyl.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jizhangyl.application.dataobject.SysMenu;
import com.jizhangyl.application.dataobject.SysRole;
import com.jizhangyl.application.dataobject.UserInfo;
import com.jizhangyl.application.dto.UserInfoDTO;

/**
 * @author 杨贤达
 * @date 2018/8/15 15:43
 * @description 操作系统用户
 */
public interface UserInfoService {

    UserInfo findByOpenid(String openid);

    /**
     * 查询用户列表
     * @return 系统用户列表
     */
    Page<UserInfo> list(Pageable pageable);
    
    /**
     * 根据用户id查询用户的详细信息:用户角色列表 + 用户菜单列表
     * @param userId 用户id
     * @return 用户的详细信息
     */
    UserInfoDTO getUserInfo(Integer userId);
    
    /**
     * 根据用户id查询用户拥有的角色列表
     * @param userId 对应UserInfo中的id
     * @return 用户拥有的角色列表
     */
    List<SysRole> listRoleByUId(Integer userId);
    
    /**
     * 根据用户id查询用户拥有的菜单列表
     * @param userId 对应UserInfo中的id
     * @return 用户拥有的菜单列表(去重后的)
     */
    List<SysMenu> listMenuByUId(Integer userId);
    
    /**
     * 添加系统用户的时候需要为其分配相应的角色
     * 
     * @param userInfo 系统用户信息
     * @param roleIds 角色列表id
     */
    void add(UserInfo userInfo, List<Integer> roleIds);

    /**
     * 逻辑删除用户
     * 
     * 1. 修改frozen为已冻结
     * 2. 删除该用户的所有角色
     * 
     * @param userId 用户id
     */
    void delete(Integer userId);

    /**
     * 更新系统用户数据
     * 
     * 1. 更新userInfo表中的数据
     * 2. 删除该用户所拥有的所有角色
     * 3. 为该用户授予新的角色
     * 
     * @param userInfo 用户信息
     * @param roleIds 角色列表
     */
    void update(UserInfo userInfo, List<Integer> roleIds);
}
