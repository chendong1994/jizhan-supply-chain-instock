package com.jizhangyl.application.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 杨贤达
 * @date 2018/8/19 20:35
 * @description
 */
public class ValidatorUtil {

    private static final Pattern PAOGOODS_PATTERN = Pattern.compile("[01]");

    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        Matcher m = MOBILE_PATTERN.matcher(src);
        return m.matches();
    }

    public static boolean isPaoGoods(Integer src) {
        if (src == null) {
            return false;
        }
        Matcher m = PAOGOODS_PATTERN.matcher(String.valueOf(src));
        return m.matches();
    }

    public static void main(String[] args) {
//        System.out.println(isMobile("15990128390"));
//        System.out.println(isMobile("1599012839"));

        System.out.println(isPaoGoods(0));
        System.out.println(isPaoGoods(1));
        System.out.println(isPaoGoods(00));
        System.out.println(isPaoGoods(01));
        System.out.println(isPaoGoods(11));
    }
}
