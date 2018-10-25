package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.VO.WxuserVO;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.WxuserService;
import com.jizhangyl.application.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/9/10 14:50
 * @description
 */
@RestController
@RequestMapping("/wxuser")
public class WxuserController {

    @Autowired
    private WxuserService wxuserService;

    @GetMapping("/inviteCodeList")
    public ResultVO inviteCodeList() {
        // 查出所有的状态正常的用户
        List<Wxuser> wxuserList = wxuserService.findByInviteCodeNotNull();

        List<WxuserVO> wxuserVOList = wxuserList.stream().map(e -> {
            WxuserVO wxuserVO = new WxuserVO();
            wxuserVO.setOpenId(e.getOpenId());
            wxuserVO.setInviteCode(e.getInviteCode());
            return wxuserVO;
        }).collect(Collectors.toList());

        return ResultVOUtil.success(wxuserVOList);
    }

    @GetMapping("/getInviteCode")
    public ResultVO getInviteCode(@RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        Wxuser wxuser = wxuserService.findByOpenId(openid);

        String inviteCode = null;
        if (wxuser != null) {
            inviteCode = wxuser.getInviteCode();
        }

        return ResultVOUtil.success(inviteCode);
    }
    
    /**
     * 根据unionId获取wxuser表中对应微信用户的openid
     * @return
     */
    @GetMapping("/getWxUserOpenIdByUnionId")
    public ResultVO getWxUserOpenIdByUnionId(@RequestParam("unionId") String unionId) {
        
        Wxuser wxuser = wxuserService.findByUnionId(unionId);
        
        return ResultVOUtil.success(wxuser.getOpenId());
    }
}
