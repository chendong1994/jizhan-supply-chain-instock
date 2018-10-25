package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.repository.primary.ExpressNumRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/26 13:10
 * @description
 */
@Component
public class ExpressNumRepositoryTest extends MainApplicationTests {

    @Autowired
    private ExpressNumRepository expressNumRepository;

    @Test
    public void countByStatus() {
        Integer result = expressNumRepository.countByStatus(0);
        Assert.assertEquals(new Integer(4999), result);
    }
}