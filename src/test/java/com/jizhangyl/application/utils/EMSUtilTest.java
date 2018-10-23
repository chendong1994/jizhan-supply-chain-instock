package com.jizhangyl.application.utils;

import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.service.ExpenseCalendarService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/8/24 23:46
 * @description
 */
@Component
public class EMSUtilTest extends MainApplicationTests {

    @Autowired
    private EMSUtil emsUtil;
    @Autowired 
    private ExpenseCalendarService expenseCalendarService;
    

    @Test
    public void query() {
        // EJ671019253JP
        JSONObject result = emsUtil.query("EK315417198HK");
        System.out.println(result.toString());
    }
}