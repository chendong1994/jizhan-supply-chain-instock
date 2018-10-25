package com.jizhangyl.application.service;

import java.util.Date;
import java.util.List;

import com.jizhangyl.application.dataobject.primary.OaBill;

/**
 * 内部oa 账单
 * @author 陈栋
 * @date 2018年10月19日  
 * @description
 * 
 */
public interface OaBillService {
	
	OaBill findByOaBillId(Integer oaBillId);
	
	List<OaBill> findByCreateTimeBetween(Date startTime, Date endTime);
	
	OaBill addOaBill(OaBill oaBill);
	
	

}
