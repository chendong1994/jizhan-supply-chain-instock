package com.jizhangyl.application.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jizhangyl.application.dataobject.OaBill;
import com.jizhangyl.application.repository.OaBillRepository;
import com.jizhangyl.application.service.OaBillService;


@Service
public class OaBillServiceImpl implements OaBillService{
	
	@Autowired
	private OaBillRepository oaBillRepository;

	@Override
	public OaBill findByOaBillId(Integer oaBillId) {
		return oaBillRepository.findOne(oaBillId);
	}

	@Override
	public List<OaBill> findByCreateTimeBetween(Date startTime, Date endTime) {
		
		if(startTime == null || endTime == null){
			return oaBillRepository.findAll();
		}else{
			return oaBillRepository.findByCreateTimeBetween(startTime, endTime);
		}
	}

	@Override
	public OaBill addOaBill(OaBill oaBill) {
		if(oaBill.getOaBillId() != null){
			OaBill bb = oaBillRepository.findOne(oaBill.getOaBillId());
			if(bb == null){
				throw new RuntimeException("参数错误");
			}
			return oaBillRepository.save(oaBill);
		}
		else{
			return oaBillRepository.save(oaBill);
		}
	}
	
	

}
