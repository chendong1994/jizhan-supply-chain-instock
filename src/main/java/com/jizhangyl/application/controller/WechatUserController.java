package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.config.ProjectUrlConfig;
import com.jizhangyl.application.constant.CookieConstant;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.dataobject.secondary.UserInfo;
import com.jizhangyl.application.enums.InviteCodeStatusEnum;
import com.jizhangyl.application.enums.ShiroEnum;
import com.jizhangyl.application.enums.UserInfoEnum;
import com.jizhangyl.application.service.UserInfoService;
import com.jizhangyl.application.service.WxuserService;
import com.jizhangyl.application.utils.CookieUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨贤达
 * @date 2018/8/3 11:19
 * @description
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class WechatUserController {

    /**
     * 微信扫码登录页面的url地址
     */
    private String loginUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=wxa54528c6aebbc825&redirect_uri=https%3A%2F%2Fwww.jizhangyl.com%2Fjizhangyl%2Fwechat%2FqrUserInfo&response_type=code&scope=snsapi_login&state=https%3A%2F%2Fwww.jizhangyl.com%2Fjizhangyl%2Fuser%2Flogin#wechat_redirect";
    
    @Autowired
    private WxuserService wxuserService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    /**
     * 检查邀请码是否正确(点击小程序中验证邀请码的"提交"按钮时会触发的请求)
     * @param code
     * @param openid
     * @return
     */
    @ResponseBody
    @GetMapping("/checkInviteCode")
    public ResultVO checkInviteCode(@RequestParam("code") String code, @RequestParam("open_id") String openid) {
        Boolean result = wxuserService.checkInviteCode(code, openid);
        Map<String, Integer> resultMap = new HashMap<>();
        if (result) {
            resultMap.put("status", InviteCodeStatusEnum.INVITE_CODE_RIGHT.getCode());
            // 邀请码存在,微信用户信息同步到系统用户表，则进行shiro的认证流程
            
            
            
        } else {
            resultMap.put("status", InviteCodeStatusEnum.INVITE_CODE_ERROR.getCode());
        }
        return ResultVOUtil.success(result);
    }

    /**
     * 登录实现
     * @param response
     * @param unionid
     * @return
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam("unionid") String unionid) {

        // 此处判断 -> 若数据库没有该用户的 openid 如何进行下一步操作（邀请码？短信验证码？）

        log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        log.info("【PC登录】当前用户unionid: " + unionid);
        log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

        // 转到shiro的登录流程去认证
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken upToken = new UsernamePasswordToken(unionid, ShiroEnum.OPEN_PWD.getCode());
        try {
            subject.login(upToken);
        } catch (UnknownAccountException e) {
            log.info("【PC登录】当前用户unionid: " + unionid + " 不存在");
            return "/user/unauth";
        } catch (IncorrectCredentialsException e) {
            log.info("【PC登录】当前unionid为: " + unionid + " 的用户的密码匹配错误");
            return "/user/unauth";
        } catch (AuthenticationException e) {
            log.info("【PC登录】当前unionid为: " + unionid + " 的用户在认证的过程中出现了其他未知错误");
            e.printStackTrace();
            return "/user/unauth";
        }
        // 1. openid 去和数据库里面的数据匹配
        UserInfo userInfo = userInfoService.findByUnionid(unionid);
        if (userInfo == null) {
            return "/common/error"; // 说明用户没有从集栈小程序端进入系统，跳转的页面应提示先进入小程序
        }

        // 账号被冻结的用户禁止登录后台
        if (UserInfoEnum.FREE.getCode().equals(userInfo.getFrozen())) {
            // 避免重复生成 token 的设置 redis
            Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
            String tokenValue = null;
            if (cookie != null) {
                tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
            }
            if (cookie == null || StringUtils.isEmpty(tokenValue)) { // 客户端传过来的cookie不为空,并且redis中的token的value也没有过期

                // 2. 设置 token 至 redis
                String token = UUID.randomUUID().toString();
                Integer expire = RedisConstant.EXPIRE;
                redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), unionid, expire, TimeUnit.SECONDS);

                // 3. 设置 token 至 cookie
                CookieUtil.set(response, CookieConstant.TOKEN, token, expire);
            }
            
            return "redirect:" + projectUrlConfig.getJizhangyl() + "/jizhangyl/index.html";
        } else {
            return "/common/error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        // 1. 从 Cookie 里查询
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie != null) {
            // 2. 清除 redis
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
            // 3. 清除 cookie
            CookieUtil.set(response, CookieConstant.TOKEN, null, 0);
        }

        // 清空SecurityManager中的subject信息
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }

        // 跳转登录
        return "redirect:" + projectUrlConfig.getJizhangyl() + "/jizhangyl/index.html";
    }

    /**
     * 小程序登录实现
     * @param response
     * @param openid
     * @return
     */
    @ResponseBody
    @GetMapping("/loginByMini")
    public ResultVO loginByMini(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestParam("openid") String openid) {

        log.info("【MiniApp登录】当前用户openid: " + openid);

        // 1. openid 去和数据库里面的数据匹配

        // 避免重复生成 token 的设置 redis
        String miniToken = request.getParameter(CookieConstant.TOKEN);
        String tokenValue = null;
        if (!StringUtils.isEmpty(miniToken)) {
            tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, miniToken));
        }
        Map resultMap = new HashMap();
        if (StringUtils.isEmpty(miniToken) || StringUtils.isEmpty(tokenValue)) {

            // 2. 设置 token 至 redis
            String token = UUID.randomUUID().toString();
            Integer expire = RedisConstant.EXPIRE;
            redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), openid, expire, TimeUnit.SECONDS);

            // 3. 设置 token 至 cookie
            resultMap.put(CookieConstant.TOKEN, token);
        }
        return ResultVOUtil.success(resultMap);
    }
    
    /**
     * 未登录,shiro应重定向到登录界面,此处返回未登录状态信息由前端控制跳转页面
     * 
     * 未认证,前往登录页面登录认证
     */
    @RequestMapping("/unauth")
    public String unauth() {
        return "redirect:"
                .concat(projectUrlConfig.getWechatOpenAuthorize())
                .concat("/jizhangyl/wechat/qrAuthorize")
                .concat("?returnUrl=")
                .concat(projectUrlConfig.getJizhangyl())
                .concat("/jizhangyl/user/login");
    }
}
