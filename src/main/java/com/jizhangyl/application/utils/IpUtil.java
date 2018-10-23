package com.jizhangyl.application.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨贤达
 * @date 2018/8/12 11:00
 * @description
 */
public class IpUtil {


    public static String getIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
