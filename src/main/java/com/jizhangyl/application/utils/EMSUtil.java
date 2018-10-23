package com.jizhangyl.application.utils;

import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.config.EMSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author 杨贤达
 * @date 2018/8/24 11:36
 * @description 快递查询工具类
 */
@Component
public class EMSUtil {

    @Autowired
    private EMSConfig emsConfig;

    /**
     * 查询
     * @param src
     * @return
     */
    public JSONObject query(String src) {
        String url = emsConfig.getUrl().concat(src);
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("version", emsConfig.getVersion());
        headers.add("authenticate", emsConfig.getAuthenticate());
        HttpEntity httpEntity = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class);

        JSONObject result = responseEntity.getBody();
        return result;
    }
}
