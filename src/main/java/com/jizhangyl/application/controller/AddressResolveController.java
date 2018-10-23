package com.jizhangyl.application.controller;

import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.AddressResolveService;
import com.jizhangyl.application.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨贤达
 * @date 2018/8/8 13:47
 * @description
 */
@RestController
@RequestMapping("/address")
public class AddressResolveController {

    @Autowired
    private AddressResolveService addressResolveService;

    @GetMapping("/resolve")
    public ResultVO resolve(@RequestParam("address") String address) {
        if (StringUtils.isEmpty(address)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        JSONObject result = addressResolveService.resolve(address);
        return ResultVOUtil.success(result);
    }
}
