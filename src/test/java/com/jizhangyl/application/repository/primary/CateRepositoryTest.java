package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.primary.Cate;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author 杨贤达
 * @date 2018/10/23 17:26
 * @description
 */
@Component
public class CateRepositoryTest extends MainApplicationTests {

    @Autowired
    private CateRepository cateRepository;

    @Test
    public void findByNameLike() {
        List<Cate> cateList = cateRepository.findByNameLike("护肤");
        Assert.assertNotEquals(0, cateList.size());
    }
}