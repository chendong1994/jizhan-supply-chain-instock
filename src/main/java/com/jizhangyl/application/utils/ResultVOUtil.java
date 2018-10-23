package com.jizhangyl.application.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.SysMenu;
import com.jizhangyl.application.dataobject.UserInfo;
import com.jizhangyl.application.dto.UserInfoDTO;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.service.UserInfoService;

/**
 * @author 杨贤达
 * @date 2018/7/27 10:07
 * @description
 */
@Component
public class ResultVOUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private UserInfoService userInfoService;
    
    public static ResultVO success(Object object) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        resultVO.setData(object);
        
        // 对于已经登录的用户,获取到他拥有的权限字符串列表
        /*Subject subject = SecurityUtils.getSubject();
        UserInfo userInfo = null;
        boolean isAuthenticated = false; // true表示已经认证过
        if (subject != null) {
            userInfo = (UserInfo) subject.getPrincipal();
            if (userInfo != null) {
                isAuthenticated = true;
            }
        }
        if (isAuthenticated) {
            UserInfoDTO userInfoDTO = userInfoService.getUserInfo(userInfo.getId());
            
            List<SysMenu> menuList = userInfoDTO.getMenuList();
            
            List<String> perms = menuList.stream().map(e -> e.getPerms()).collect(Collectors.toList());
            
            resultVO.setPerms(perms);
        }*/
        
        return resultVO;
    }

    public static ResultVO success() {
        return success(null);
    }

    public static ResultVO error(Integer code, String msg) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }

    public static ResultVO error(ResultEnum resultEnum) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMsg(resultEnum.getMessage());
        return resultVO;
    }
}
