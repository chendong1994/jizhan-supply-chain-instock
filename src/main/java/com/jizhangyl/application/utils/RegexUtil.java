package com.jizhangyl.application.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 杨贤达
 * @date 2018/9/7 11:36
 * @description 中文姓名正则校验
 */
public class RegexUtil {

    private static final Pattern ZH_NAME_PATTERN = Pattern.compile("^(?!.*?(先生|女士|小姐|叔叔|阿姨)$)[\\u4e00-\\u9fa5]{2,4}+$");

    public static boolean isZhName(String src) {

        Matcher matcher = ZH_NAME_PATTERN.matcher(src);

        return matcher.matches();

    }

    public static void main(String[] args) {
        System.out.println(isZhName("杨阿姨"));
        System.out.println(isZhName("杨叔叔"));
        System.out.println(isZhName("杨小姐"));
        System.out.println(isZhName("杨贤达"));
        System.out.println(isZhName("杨先生"));
        System.out.println(isZhName("杨女士"));
        System.out.println(isZhName("biubiu~"));
        System.out.println(isZhName("MR杨"));
        System.out.println(isZhName("杨MR"));
        System.out.println(isZhName("MR杨MR"));
        System.out.println(isZhName("杨MR杨"));
        System.out.println(isZhName("陈谦"));
        System.out.println(isZhName("武鹏真"));
        System.out.println(isZhName("黄凯"));
        System.out.println(isZhName("黄凯凯凯"));
        System.out.println(isZhName("黄凯凯凯凯"));

        System.out.println(isZhName("王冬雪"));
        System.out.println(isZhName("刘峥"));
        System.out.println(isZhName("陈洁"));

    }
}
