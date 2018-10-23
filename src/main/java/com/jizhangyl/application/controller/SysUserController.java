package com.jizhangyl.application.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.UserInfo;
import com.jizhangyl.application.dto.UserInfoDTO;
import com.jizhangyl.application.service.SysRoleService;
import com.jizhangyl.application.service.SysUserRoleService;
import com.jizhangyl.application.service.UserInfoService;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年10月15日 上午11:01:35
 * @description 处理对系统用户的请求
 */
@Slf4j
@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @GetMapping("/list")
    public ResultVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "20") Integer size) {

        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = new PageRequest(page - 1, size, sort);
        Page<UserInfo> pageList = userInfoService.list(pageRequest);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userList", pageList.getContent());
        resultMap.put("totalPage", pageList.getTotalPages());
        resultMap.put("totalItems", pageList.getTotalElements());

        return ResultVOUtil.success(resultMap);
    }

    @GetMapping("/getUserInfoByUserID")
    public ResultVO getUserInfoByUserID(@RequestParam("userId") Integer userId) {
        
        UserInfoDTO userInfoDTO = userInfoService.getUserInfo(userId);
        
        return ResultVOUtil.success(userInfoDTO);
    }
    
    @GetMapping("/freeze")
    public ResultVO delete(@RequestParam("userId") Integer userId) {

        /**
         * 将该账号冻结,逻辑删除
         */
        userInfoService.delete(userId);

        return ResultVOUtil.success();
    }

    @GetMapping("/update")
    public ResultVO update(UserInfo userInfo, @RequestParam("roleIds") String roleIds) {

        List<Integer> ids = JSONArray.parseArray(roleIds, Integer.class);

        userInfoService.update(userInfo, ids);;

        return ResultVOUtil.success();
    }
}
