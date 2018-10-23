package com.jizhangyl.application.controller;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.config.ProjectUrlConfig;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.enums.WechatUserStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.WxuserService;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨贤达
 * @date 2018/8/3 22:24
 * @description
 */
@Slf4j
@Controller
@RequestMapping("/wechat")
public class WechatController {

    @Autowired
    private WxMpService wxOpenService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @Autowired
    private WxuserService wxuserService;

    @Autowired
    private WxMaUserService wxMaUserService;

    /**
     * 访问此接口触发微信扫码登录(前往微信二维码扫码页面) /qrconnect...
     * @param returnUrl
     * @return
     */
    @GetMapping("/qrAuthorize")
    public String qrAuthorize(@RequestParam("returnUrl") String returnUrl) {
        String url = projectUrlConfig.getWechatOpenAuthorize() + "/jizhangyl/wechat/qrUserInfo";
        String redirectUrl = null;
        try {
            redirectUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QrConnectScope.SNSAPI_LOGIN,
                    URLEncoder.encode(returnUrl, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("【扫码登录】{}", e);
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * 扫码之后会触发的请求
     * @param code 里面有access_token,access_token里面有openid
     * @param returnUrl 即https://www.jizhangyl.com/jizhangyl/user/login
     * @return
     */
    @GetMapping("/qrUserInfo")
    public String qrUserInfo(@RequestParam("code") String code,
                             @RequestParam("state") String returnUrl) {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxOpenService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", e);
            throw new GlobalException(ResultEnum.WECHAT_ERROR.getCode(), e.getError().getErrorMsg());
        }
        log.info("wxMpOAuth2AccessToken = {}", wxMpOAuth2AccessToken);
        String openId = wxMpOAuth2AccessToken.getOpenId();
        
        
        String unionId = wxMpOAuth2AccessToken.getUnionId(); // 后续将替换成使用unionId来进行登录
        log.info("【$$$$$$$$$$$$$$$$$】");
        log.info("【$$$$$$$$$$$$$$$$$】");
        log.info("【$$$$$ PC端的unionId:" + unionId + " $$$$$】");
        log.info("【$$$$$$$$$$$$$$$$$】");
        log.info("【$$$$$$$$$$$$$$$$$】");
        
        return "redirect:" + returnUrl + "?openid=" + openId;
    }

    /**
     * 进入小程序的微信授权(点击小程序中"授权按钮"的时候会访问的请求)
     * @param js_code
     * @param encryptedData
     * @param iv
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getOpenid")
    public ResultVO getOpenid(@RequestParam("js_code") String js_code,
                              @RequestParam("encryptedData") String encryptedData,
                              @RequestParam("iv") String iv) throws Exception {
        // new implementation.
        WxMaJscode2SessionResult wxMaJscode2SessionResult = wxMaUserService.getSessionInfo(js_code);
        if (wxMaJscode2SessionResult == null) {
            log.error("【微信授权】授权失败, Session信息获取失败");
            throw new GlobalException(ResultEnum.WECHAT_ERROR);
        }
        WxMaUserInfo wxMaUserInfo = wxMaUserService.getUserInfo(wxMaJscode2SessionResult.getSessionKey(), encryptedData, iv);
        JSONObject userInfo = JSONObject.fromObject(wxMaUserInfo);

        // 新用户入库
        WechatUserStatusEnum wechatUserStatusEnum = wxuserService.isExist(userInfo);

        // 如果微信用户的状态为:已激活(ACTIVATED),则进行shiro的认证流程
        /*if (wechatUserStatusEnum.getCode().equals(WechatUserStatusEnum.ACTIVATED.getCode())) {
            
        }*/
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", wechatUserStatusEnum.getCode());
        result.put("userInfo", userInfo);

        return ResultVOUtil.success(result);
    }
}

