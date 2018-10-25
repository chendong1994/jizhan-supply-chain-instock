package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dto.BrandDTO;
import com.jizhangyl.application.repository.primary.BrandRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/3 9:59
 * @description
 */
@Component
public class BrandRepositoryTest extends MainApplicationTests {

    @Autowired
    private BrandRepository repository;

    @Test
    public void findByIdIn() {
    }

    @Test
    public void findAllIdAndName() {
        List<BrandDTO> brandDTOList = repository.findAllIdAndName();
        Assert.assertNotEquals(0, brandDTOList.size());
    }
}