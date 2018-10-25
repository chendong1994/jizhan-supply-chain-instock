package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.primary.ExpressNum;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 杨贤达
 * @date 2018/8/25 16:47
 * @description
 */
@Component
public class ExpressNumServiceImplTest extends MainApplicationTests {

    @Autowired
    private ExpressNumServiceImpl expressNumService;

    @Test
    public void findUnused() {
        ExpressNum expressNum = expressNumService.findUnused();
        Assert.assertNotNull(expressNum);
    }

    @Test
    @Transactional
    public void findAndDeleteAndInsert() {

        // id: 6 -> 单号: BS057123951CN
        ExpressNum expressNum = expressNumService.findOne(6);

        expressNumService.delete(expressNum.getId());

        // 插入到最后一条记录
        ExpressNum record = new ExpressNum();
        record.setExpNum(expressNum.getExpNum());
        expressNumService.save(record);
    }
}