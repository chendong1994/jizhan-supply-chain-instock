package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.WxuserAddr;
import com.jizhangyl.application.enums.AddrTypeEnum;
import com.jizhangyl.application.service.WxuserAddrService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author 杨贤达
 * @date 2018/8/5 16:23
 * @description
 */
@Component
public class WxuserAddrServiceImplTest extends MainApplicationTests {

    @Autowired
    private WxuserAddrService wxuserAddrService;

    @Test
    public void findAll() {
    }

//    @Test
//    public void saveBatch() {
//        WxuserAddr wxuserAddr = new WxuserAddr();
//        wxuserAddr.setWxuserId(222222);
//        wxuserAddr.setReceiver("黄总");
//        wxuserAddr.setPhone("13912345678");
//        wxuserAddr.setArea("杭州，滨江，浦沿");
//        wxuserAddr.setDetailAddr("江南大道口");
//        wxuserAddr.setAddrLabel("自定义标签");
//        wxuserAddr.setIsDefault(AddrTypeEnum.DEFAULT_ADDRESS.getCode().shortValue());
//
//        int result = wxuserAddrService.saveBatch(Arrays.asList(wxuserAddr));
//        Assert.assertEquals(1, result);
//    }

    @Test
    public void saveBatch() {

        List<WxuserAddr> addrList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            WxuserAddr wxuserAddr = new WxuserAddr();
            String openid = UUID.randomUUID().toString();
            wxuserAddr.setOpenid(openid);
            wxuserAddr.setReceiver("陈总");
            wxuserAddr.setPhone("18888998899");
            wxuserAddr.setArea("杭州，滨江，浦沿");
            wxuserAddr.setDetailAddr("江南大道口");
            wxuserAddr.setAddrLabel("Jizhan Group");
            wxuserAddr.setIsDefault(AddrTypeEnum.DEFAULT_ADDRESS.getCode().shortValue());
            addrList.add(wxuserAddr);
        }
        int result = wxuserAddrService.saveBatch(addrList);
        Assert.assertEquals(addrList.size(), result);
    }
}