package com.jizhangyl.application.controller;

import com.jizhangyl.application.config.ProjectUrlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 杨贤达
 * @date 2018/8/29 14:46
 * @description
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @RequestMapping("/test")
    public String test() {
        return "/login_fail";
    }
}
