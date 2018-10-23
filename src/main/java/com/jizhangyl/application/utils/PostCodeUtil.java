package com.jizhangyl.application.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

/**
 * @author 杨贤达
 * @date 2018/8/27 16:51
 * @description
 */
@Slf4j
public class PostCodeUtil {

    public static String getPostCodeByAddr(String addr) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://cpdc.chinapost.com.cn/web/index.php?m=postsearch&c=index&a=ajax_addr&searchkey=" + addr;
        String response = restTemplate.getForObject(url, String.class);
        log.info("response = {}", response);

        return null;
    }

    public static void main(String[] args) {
        getPostCodeByAddr("广东省深圳市宝安区西乡街道");
    }
}
