package com.jizhangyl.application.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.config.AddressResolveConfig;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨贤达
 * @date 2018/8/7 10:03
 * @description
 */
@Slf4j
@Service
public class AddressResolveService {

    @Autowired
    private AddressResolveConfig addressResolveConfig;

    private static final String TEST_DATA = "{\"text\":\"浙江省绍兴市诸暨市浣东街道西子公寓北区电话：13905857430  衣服  食物 \", \"multimode\":false}";

    public JSONObject resolve(String originData) {
        JSONObject result = null;
        String appId = addressResolveConfig.getAppId();
        String method= "cloud.address.resolve";
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String sign =  appId + method + ts + addressResolveConfig.getApiKey();
        String encryptData = DigestUtils.md5Hex(sign);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();

        bodys.put("app_id", appId);
        bodys.put("method", method);
        bodys.put("ts", ts);
        bodys.put("sign", encryptData);

        // "\"浙江省绍兴市诸暨市浣东街道西子公寓北区电话：13905857430  衣服  食物 \""
        originData = originData.replace("\n", "");
        String data = "{\"text\":" + "\"" + originData + "\"" + ", \"multimode\":false}";
        log.info("data: {}", data);
        bodys.put("data", data);

        try {
            HttpResponse response = HttpUtils.doPost(addressResolveConfig.getHost(), addressResolveConfig.getPath(), method, headers, querys, bodys);
            String jsonStr = EntityUtils.toString(response.getEntity());
            result = JSON.parseObject(jsonStr);
            log.info("ResponseBody = {}", jsonStr);
        } catch(Exception e) {
            throw new GlobalException(ResultEnum.ADDR_RESOLVE_ERROR);
        }
        return result;
    }
    
    /**
     * 批量解析
     * @param originData
     * @return
     */
    public JSONObject resolveBatch(String originData) {
        JSONObject result = null;
        String appId = addressResolveConfig.getAppId();
        String method= "cloud.address.resolve";
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String sign =  appId + method + ts + addressResolveConfig.getApiKey();
        String encryptData = DigestUtils.md5Hex(sign);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();

        bodys.put("app_id", appId);
        bodys.put("method", method);
        bodys.put("ts", ts);
        bodys.put("sign", encryptData);

        // "\"浙江省绍兴市诸暨市浣东街道西子公寓北区电话：13905857430  衣服  食物 \""
//        originData = originData.replace("\n", "");
        String data = "{\"text\":" + "\"" + originData + "\"" + ", \"multimode\":true}";
        log.info("data: {}", data);
        bodys.put("data", data);

        try {
            HttpResponse response = HttpUtils.doPost(addressResolveConfig.getHost(), addressResolveConfig.getPath(), method, headers, querys, bodys);
            String jsonStr = EntityUtils.toString(response.getEntity());
            result = JSON.parseObject(jsonStr);
            log.info("ResponseBody = {}", jsonStr);
        } catch(Exception e) {
            throw new GlobalException(ResultEnum.ADDR_RESOLVE_ERROR);
        }
        return result;
    }
    
}
