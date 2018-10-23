package com.jizhangyl.application.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.VO.SysRoleVO;
import com.jizhangyl.application.dataobject.SysRole;
import com.jizhangyl.application.dataobject.SysUserRole;
import com.jizhangyl.application.dto.SysRoleDTO;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.SysRoleService;
import com.jizhangyl.application.service.SysUserRoleService;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年10月12日 下午2:20:29
 * @description 处理对角色的请求
 */
@Slf4j
@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @GetMapping("/list")
    public ResultVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "20") Integer size) {

        PageRequest pageRequest = new PageRequest(page - 1, size);

        Page<SysRole> roleList = sysRoleService.findAll(pageRequest);

        List<SysRoleVO> roleVOList = new ArrayList<SysRoleVO>(roleList.getContent().size());

        roleVOList = roleList.getContent().stream().map(e -> {
            SysRoleVO sysRoleVO = new SysRoleVO();
            BeanUtils.copyProperties(e, sysRoleVO);
            String createTime = DateUtil.dateToString(e.getCreateTime());
            String updateTime = DateUtil.dateToString(e.getUpdateTime());
            sysRoleVO.setCreateTime(createTime);
            sysRoleVO.setUpdateTime(updateTime);
            return sysRoleVO;
        }).collect(Collectors.toList());

        Map<String, Object> resultMap = new HashMap<String, Object>(roleList.getContent().size());
        resultMap.put("data", roleVOList);
        resultMap.put("totalPage", roleList.getTotalPages());
        resultMap.put("totalItems", roleList.getNumberOfElements());

        return ResultVOUtil.success(resultMap);
    }

    @GetMapping("/getRoleInfo")
    public ResultVO getRoleInfo(@RequestParam("roleId") Integer roleId) {
        
        SysRoleDTO roleInfo = sysRoleService.findRoleInfo(roleId);
        
        return ResultVOUtil.success(roleInfo);
    }
    
    @GetMapping("/add")
    public ResultVO add(@RequestParam(value = "name", required = true) String name,
                        @RequestParam(value = "remark", required = true) String remark,
                        @RequestParam(value = "menuIds", required = true) String menuIds) {

        SysRole role = new SysRole();
        role.setName(name);
        role.setRemark(remark);
        role.setUpdateBy("oYAqI1BOwVet_Cjl6a-6lqhJUzlY"); // 集成shiro后从Subject中取openid

        List<Integer> ids = JSONArray.parseArray(menuIds, Integer.class);

        sysRoleService.add(role, ids);

        return ResultVOUtil.success();
    }

    @GetMapping("/delete")
    public ResultVO delete(@RequestParam Integer id) {

        if (id == null) {
            log.info("【删除角色】角色id为空");
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        if (sysRoleService.findOne(id) == null) {
            log.info("【删除角色】要删除的角色不存在");
            throw new GlobalException(ResultEnum.ROLE_NOT_EXIST);
        }

        List<SysUserRole> userRoleList = sysUserRoleService.findByRoleId(id);
        if (userRoleList.size() > 0) {
            log.info("【删除角色】有用户拥有id为" + id + "的角色,不能删除该角色");
            throw new GlobalException(ResultEnum.USER_HAS_ROLES);
        }

        sysRoleService.delete(id);

        return ResultVOUtil.success();
    }

    @GetMapping("/update")
    public ResultVO update(@RequestParam(value = "id", required = true) Integer id,
                           @RequestParam(value = "name", required = true) String name,
                           String remark,
                           @RequestParam(value = "menuIds", required = true) String menuIds) {

        SysRole role = sysRoleService.findOne(id);
        if (role == null) {
            log.info("【更新角色】不存在id为 " + id + " 的角色");
            throw new GlobalException(ResultEnum.ROLE_NOT_EXIST);
        }

        if (!role.getName().equals(name) && sysRoleService.findByName(name) != null) {
            log.info("【更新角色】要修改的角色名称已经存在");
            throw new GlobalException(ResultEnum.ROLE_IS_EXIST.getCode(), String.format(ResultEnum.ROLE_IS_EXIST.getMessage(), name));
        }

        role.setName(name);
        role.setRemark(remark);
        role.setUpdateBy("oYAqI1BOwVet_Cjl6a-6lqhJUzlY"); // 集成shiro后从Subject中取openid

        List<Integer> ids = JSONArray.parseArray(menuIds, Integer.class);

        sysRoleService.update(role, ids);

        return ResultVOUtil.success();
    }

}
