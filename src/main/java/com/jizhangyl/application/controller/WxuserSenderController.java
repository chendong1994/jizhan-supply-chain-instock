package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.WxuserSender;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.WxuserSenderForm;
import com.jizhangyl.application.form.WxuserSenderUpdateForm;
import com.jizhangyl.application.service.WxuserSenderService;
import com.jizhangyl.application.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/7 17:04
 * @description
 */
@RestController
@RequestMapping("/sender")
public class WxuserSenderController {

    @Autowired
    private WxuserSenderService senderService;

    /**
     * 获取发件人地址列表（去除分页）
     * @param openid
     * @return
     */
    @GetMapping("/findAllByOpenid")
    public ResultVO findAllByOpenid(@RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<WxuserSender> wxuserSenderList = senderService.findAllByOpenid(openid);
        return ResultVOUtil.success(wxuserSenderList);
    }

    @GetMapping("/delete")
    public ResultVO delete(@RequestParam("openid") String openid,
                           @RequestParam("id") Integer id) {
        if (StringUtils.isEmpty(openid) || id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        senderService.delete(openid, id);
        return ResultVOUtil.success();
    }

    @PostMapping("/save")
    public ResultVO save(@Valid WxuserSenderForm wxuserSenderForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        WxuserSender wxuserSender = senderService.save(wxuserSenderForm);
        return ResultVOUtil.success(wxuserSender);
    }

    @PostMapping("/update")
    public ResultVO update(@Valid WxuserSenderUpdateForm wxuserSenderUpdateForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        WxuserSender wxuserSender = senderService.update(wxuserSenderUpdateForm);
        return ResultVOUtil.success(wxuserSender);
    }

    @GetMapping("/setDefault")
    public ResultVO setDefault(@RequestParam("openid") String openid,
                               @RequestParam("id") Integer id) {
        if (StringUtils.isEmpty(openid) || id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        senderService.setDefaultSender(openid, id);

        return ResultVOUtil.success();
    }

    @GetMapping("/findById")
    public ResultVO findById(@RequestParam("id") Integer senderId) {
        if (senderId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return ResultVOUtil.success(senderService.findOne(senderId));
    }

    @GetMapping("/getDefault")
    public ResultVO getDefault(@RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        WxuserSender wxuserSender = senderService.findSenderAddrByOpenid(openid);
        return ResultVOUtil.success(wxuserSender);
    }

}
