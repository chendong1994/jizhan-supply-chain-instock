package com.jizhangyl.application.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Objects;

/**
 * @author 杨贤达
 * @date 2018/8/3 14:54
 * @description Json 工具类
 */
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static GsonBuilder gsonBuilder = new GsonBuilder();

    /**
     * Convert target object to json string.
     *
     * @param obj target object.
     * @return converted json string.
     */
    public static String toJson(Object obj) {
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create().toJson(obj);
    }

    public static <T> T toObject(String json, Class<T> valueType) {
        Objects.requireNonNull(json, "json is null.");
        Objects.requireNonNull(valueType, "value type is null.");

        try {
            return mapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new IllegalStateException("fail to convert [" + json + "] to [" + valueType + "].");
        }
    }

    /**
     * 过滤 json 字符串 key 中的引号
     * @param src
     * @return
     */
    public static String toJsonString(String src) {
        return src.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
    }

    public static void main(String[] args) {
        String json = "{\"name\":\"value\"}";
        String result = toJsonString(toJsonString(json));
        System.out.println(result);
    }
}
