package com.jizhangyl.application.repository.secondary;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/10/23 17:22
 * @description
 */
@Component
public class WxuserRepositoryTest extends MainApplicationTests {

    @Autowired
    private WxuserRepository wxuserRepository;

    @Test
    public void findByInviteCode() {
        Wxuser wxuser = wxuserRepository.findByInviteCode("Y666666");
        Assert.assertNotNull(wxuser);
    }
}