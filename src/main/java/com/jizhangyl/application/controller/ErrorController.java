package com.jizhangyl.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 杨贤达
 * @date 2018/8/16 9:49
 * @description 自定义错误页面覆盖 spring boot 中的错误页面
 */
@Controller
public class ErrorController {
    @RequestMapping("/400")
    public String badRequest() {
        return "common/400";
    }

    @RequestMapping("/404")
    public String notFound() {
        return "common/404";
    }

    @RequestMapping("/500")
    public String serverError() {
        return "common/500";
    }
}