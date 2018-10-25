package com.jizhangyl.application.config;

import com.jizhangyl.application.MainApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * @author 杨贤达
 * @date 2018/10/23 14:53
 * @description
 */
@Component
public class DataSourceConfigTest extends MainApplicationTests {

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Test
    public void test() {
        DataSource primaryDs = dataSourceConfig.primaryDataSource();
        DataSource secondaryDs = dataSourceConfig.secondaryDataSource();
        System.out.println("over.");
    }
}