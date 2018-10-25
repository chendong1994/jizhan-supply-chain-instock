package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.enums.AddrTypeEnum;
import com.jizhangyl.application.repository.primary.WxuserAddrRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/5 15:56
 * @description
 */
@Component
public class WxuserAddrRepositoryTest extends MainApplicationTests {

    @Autowired
    private WxuserAddrRepository repository;

    @Test
    public void save() {
        WxuserAddr wxuserAddr = new WxuserAddr();
        wxuserAddr.setOpenid("222222");
        wxuserAddr.setReceiver("黄总");
        wxuserAddr.setPhone("13912345678");
        wxuserAddr.setArea("杭州，滨江，浦沿");
        wxuserAddr.setAddrLabel("自定义标签");
        wxuserAddr.setIsDefault(AddrTypeEnum.DEFAULT_ADDRESS.getCode().shortValue());

        List<WxuserAddr> result = repository.save(Arrays.asList(wxuserAddr));
        Assert.assertNotEquals(0, result.size());

    }

    @Test
    public void findByReceiverOrReceiverNickname() throws Exception {
        String name = "杨";
        List<WxuserAddr> result = repository.findByReceiverOrReceiverNickname(name);
        Assert.assertNotEquals(0, result.size());
    }

    @Test
    public void findAllByOpenid() {
        PageRequest request = new PageRequest(0, 5);
        Page<WxuserAddr> wxuserAddrPage = repository.findAllByOpenid("111111", request);
        Assert.assertNotEquals(0, wxuserAddrPage.getTotalElements());

    }
}