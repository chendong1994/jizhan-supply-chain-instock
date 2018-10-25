package com.jizhangyl.application.repository.primary;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.primary.OaBill;


public interface OaBillRepository extends JpaRepository<OaBill, Integer> {

	
	List<OaBill> findByCreateTimeBetween(Date startTime, Date endTime);
	
	
	
}
