package com.jizhangyl.application.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨贤达
 * @date 2018/8/30 16:41
 * @description
 */
public class DevelopPermitUtil {

    public static boolean access(HttpServletRequest request) {
        String type = request.getParameter("type");
        if (!StringUtils.isEmpty(type) && type.equals("develop")) {
            return true;
        }
        return false;
    }
}
