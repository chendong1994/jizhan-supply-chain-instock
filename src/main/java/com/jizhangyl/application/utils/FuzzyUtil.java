package com.jizhangyl.application.utils;

/**
 * @author 杨贤达
 * @date 2018/8/25 17:21
 * @description
 */
public class FuzzyUtil {

    public static String mobileFuzzy(String mobile) {
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String addrFuzzy(String addr) {
        return addr.replaceAll(".{0,}(.{2})", "****$1");
    }

    public static String nameFuzzy(String name) {
        String regx = ".{1,3}(.{1})";
        String replacement = "";
        replacement = name.length() == 2 ? "*$1" : (name.length() == 3 ? "**$1" : "***$1");
        return name.replaceAll(regx, replacement);
    }

    public static void main(String[] args) {
        String mobile = "15990128390";
        System.out.println(mobileFuzzy(mobile));

        String addr = "西溪联合科技广场5号楼305室";
        System.out.println(addrFuzzy(addr));

        System.out.println(nameFuzzy("黄凯"));
        System.out.println(nameFuzzy("杨贤达"));
        System.out.println(nameFuzzy("欧阳陈谦"));
    }
}
