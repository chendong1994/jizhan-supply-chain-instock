package com.jizhangyl.application.service;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.utils.JsonUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/13 20:08
 * @description
 */
@Component
public class AddressResolveServiceTest extends MainApplicationTests {

    @Autowired
    private AddressResolveService resolveService;

    @Test
    public void resolve() {
        String p1 = "山东省 青岛市 即墨区 温泉街道 三盛国际海岸办公室（小铁门楼梯口） ，266200";
        String p2 = "江苏省 无锡市 宜兴市 丁蜀镇 南环路中宜兴北鹰电子有限公司 ，214200";
        String p3 = "广东省 深圳市 龙岗区 横岗街道 牛始埔路和悦居7栋3M ，518116";
        System.out.println(JsonUtil.toJson(resolveService.resolve(p1)));
        System.out.println(JsonUtil.toJson(resolveService.resolve(p2)));
        System.out.println(JsonUtil.toJson(resolveService.resolve(p3)));
    }
}