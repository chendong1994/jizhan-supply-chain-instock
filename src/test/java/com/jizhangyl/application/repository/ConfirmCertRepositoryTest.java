package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.ConfirmCert;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author 杨贤达
 * @date 2018/9/19 11:42
 * @description
 */
@Component
public class ConfirmCertRepositoryTest extends MainApplicationTests {

    @Autowired
    private ConfirmCertRepository confirmCertRepository;

    @Test
    public void findByConfirmId() {
    }

    @Test
    public void findByConfirmIdIn() {
        List<String> confirmIdList = Arrays.asList("1537323683831260637", "1537323566092383678", "1537323495091952142");
        List<ConfirmCert> result = confirmCertRepository.findByConfirmIdIn(confirmIdList);
        Assert.assertNotEquals(0, result.size());
    }
}