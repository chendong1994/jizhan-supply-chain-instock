package com.jizhangyl.application.service.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dto.AllDataDTO;

@Component
public class CountDataServiceImplTest extends MainApplicationTests {

	@Autowired
    private CountDataServiceImpl countDataService;

    @Test
    public void testCountAllData() {
    	String beginDate = "2018-09-18 00:00:00";
    	String endDate = "2018-09-18 19:42:19";

    	AllDataDTO allDataDTO = countDataService.countAllData(beginDate, endDate);

    	System.out.println(allDataDTO);
    }

    @Test
    public void tretCountShopSales() {
    	String beginDate = "2018-09-18 00:00:00";
    	String endDate = "2018-09-18 19:42:19";
    	countDataService.countShopSales(beginDate, endDate);
    }
    
}