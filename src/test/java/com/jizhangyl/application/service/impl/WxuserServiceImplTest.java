package com.jizhangyl.application.service.impl;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dto.BuyerSalesDTO;
import com.jizhangyl.application.service.WxuserService;

/**
 * @author 曲健磊
 * @date 2018年9月18日 上午12:24:24
 * @description 测试WxuserService中新增的方法
 */
@Component
public class WxuserServiceImplTest extends MainApplicationTests {

	@Autowired
    private WxuserService wxuserService;

	/**
	 * 测试查询买手的销售额
	 */
	@Test
	public void testfindBuyerSalesTest() {
		/*List<BuyerSalesDTO> buyerSalesDTO = wxuserService.findBuyerSales();
		
		for (int i = 0; i < buyerSalesDTO.size(); i++) {
			System.out.println(buyerSalesDTO.get(i));
		}*/
	}
}
