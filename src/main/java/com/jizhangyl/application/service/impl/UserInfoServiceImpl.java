package com.jizhangyl.application.service.impl;

import java.util.ArrayList;
import java.util.Date;
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

import com.alibaba.druid.util.StringUtils;
import com.jizhangyl.application.dataobject.primary.SysMenu;
import com.jizhangyl.application.dataobject.primary.SysRole;
import com.jizhangyl.application.dataobject.primary.SysRoleMenu;
import com.jizhangyl.application.dataobject.primary.SysUserRole;
import com.jizhangyl.application.dataobject.secondary.UserInfo;
import com.jizhangyl.application.dto.UserInfoDTO;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.SysMenuRepository;
import com.jizhangyl.application.repository.primary.SysRoleMenuRepository;
import com.jizhangyl.application.repository.primary.SysRoleRepository;
import com.jizhangyl.application.repository.primary.SysUserRoleRepository;
import com.jizhangyl.application.repository.secondary.UserInfoRepository;
import com.jizhangyl.application.service.UserInfoService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨贤达
 * @date 2018/8/15 15:43
 * @description
 */
@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysMenuRepository sysMenuRepository;

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;

    @Autowired
    private SysRoleMenuRepository sysRoleMenuRepository;

    @Override
    public UserInfo findByOpenid(String openid) {
        return userInfoRepository.findByOpenid(openid);
    }

    /**
     * 查询用户列表
     * @return 系统用户列表
     */
    @Override
    public Page<UserInfo> list(Pageable pageable) {
        Page<UserInfo> pageList = userInfoRepository.findAll(pageable);
        return pageList;
    }

    /**
     * 根据用户id查询用户的详细信息:用户角色列表 + 用户菜单列表
     * @param userId 用户id
     * @return 用户的详细信息
     */
    @Override
    public UserInfoDTO getUserInfo(Integer userId) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        UserInfo userInfo = userInfoRepository.findOne(userId);
        if (userInfo == null) {
            log.info("【id为" + userId + "的用户不存在】");
            throw new GlobalException();
        }
        
        BeanUtils.copyProperties(userInfo, userInfoDTO);
        
        List<SysRole> roleList = listRoleByUId(userId);
        List<SysMenu> menuList = listMenuByUId(userId);
        userInfoDTO.setRoleList(roleList);
        userInfoDTO.setMenuList(menuList);
        
        return userInfoDTO;
    }
    
    /**
     * 根据用户id查询用户拥有的角色列表
     * @param userId 对应UserInfo中的id
     * @return 用户拥有的角色列表
     */
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<SysRole> listRoleByUId(Integer userId) {

        List<SysUserRole> userRoleList = sysUserRoleRepository.findByUserId(userId);

        List<Integer> roleIds = userRoleList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());

        List<SysRole> roleList = sysRoleRepository.findByIdIn(roleIds);

        return roleList;
    }
    
    /**
     * 根据用户id查询用户拥有的菜单列表
     * @param userId 对应UserInfo中的id
     * @return 用户拥有的菜单列表(去重后的)
     */
    @Override
    public List<SysMenu> listMenuByUId(Integer userId) {
        // 1. 先查出用户拥有的所有角色id
        List<SysUserRole> userRoleList = sysUserRoleRepository.findByUserId(userId);
        
        List<Integer> roleIds = userRoleList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
        
        // 2. 再查出每个角色拥有的菜单列表
        List<SysMenu> menuList = new ArrayList<SysMenu>(66);
        for (Integer roleId : roleIds) {
            
            List<SysRoleMenu> roleMenuList = sysRoleMenuRepository.findByRoleId(roleId);
            
            List<Integer> menuIds = roleMenuList.stream().map(e -> e.getMenuId()).collect(Collectors.toList());
            
            List<SysMenu> tempMenuList = sysMenuRepository.findByIdIn(menuIds);
            
            menuList.addAll(tempMenuList);
        }
        
        // 3. 去重
        menuList = menuList.stream().distinct().collect(Collectors.toList());
        
        return menuList;
    }
    
    /**
     * 添加系统用户的时候需要为其分配相应的角色
     * @param userInfo 系统用户信息
     * @param roleIds 角色列表id
     */
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void add(UserInfo userInfo, List<Integer> roleIds) {
        if (userInfo == null || userInfo.getUsername() == null || userInfo.getOpenid() == null) {
            log.info("【新增系统用户】用户名和openid为空");
            throw new GlobalException();
        }

        if (!StringUtils.isEmpty(userInfo.getEmpNo())) { // 职工号不能重复
            UserInfo uInfo = userInfoRepository.findByEmpNo(userInfo.getEmpNo());
            if (uInfo != null) {
                throw new GlobalException(ResultEnum.EMPNO_EXIST);
            }
        }

        userInfoRepository.save(userInfo);

        if (roleIds != null && roleIds.size() > 0) {
            for (Integer roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userInfo.getId());
                userRole.setRoleId(roleId);
                sysUserRoleRepository.save(userRole); // 向用户-角色对应表中添加相应记录
            }
        }
    }

    /**
     * 逻辑删除用户
     * 1. 修改frozen为已冻结
     * 2. 删除该用户的所有角色
     * @param userId
     */
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Integer userId) {
        if (userId == null) {
            log.info("【逻辑删除用户】用户id为空");
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        UserInfo userInfo = userInfoRepository.findOne(userId);
        if (userInfo == null) {
            throw new GlobalException(ResultEnum.USER_NOT_EXIST);
        }

        userInfo.setFrozen(1);
        userInfoRepository.save(userInfo);
        sysUserRoleRepository.removeByUserId(userId); // 用户-角色对应关系需进行物理删除
    }

    /**
     * 更新系统用户数据(不支持冻结操作,若想实现冻请转至delete方法)
     * 
     * 1. 更新userInfo表中的数据
     * 2. 删除该用户所拥有的所有角色
     * 3. 为该用户授予新的角色
     * 
     * @param userInfo 用户信息
     * @param roleIds 角色列表
     */
    @Override
    public void update(UserInfo userInfo, List<Integer> roleIds) {
        if (userInfo == null || userInfo.getId() == null || userInfo.getRole() == null) {
            log.info("【更新用户】用户的id或身份为空");
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        UserInfo user = userInfoRepository.findOne(userInfo.getId());
        if (user == null) {
            log.info("【更新用户】要更新的用户在数据库中不存在原始记录");
            throw new GlobalException(ResultEnum.USER_NOT_EXIST);
        }

        // 判断职工号是否被占用
        if (userInfo.getEmpNo() != null && !userInfo.getEmpNo().equals(user.getEmpNo())) {
            UserInfo temp = userInfoRepository.findByEmpNo(userInfo.getEmpNo());
            if (temp != null) {
                throw new GlobalException(ResultEnum.EMPNO_EXIST);
            }
        }

        user.setUsername(userInfo.getUsername());
        user.setHead(userInfo.getHead());
        user.setOpenid(userInfo.getOpenid());
        user.setRole(userInfo.getRole());
        user.setUpdateTime(new Date());
        user.setEmpNo(userInfo.getEmpNo());
        user.setDeptNo(userInfo.getDeptNo());

        userInfoRepository.save(user);
        sysUserRoleRepository.removeByUserId(user.getId()); // 用户-角色对应关系需进行物理删除
        if (roleIds != null && roleIds.size() > 0) {
            for (Integer roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                sysUserRoleRepository.save(userRole); // 向用户-角色对应表中添加相应记录
            }
        }
    }
}
