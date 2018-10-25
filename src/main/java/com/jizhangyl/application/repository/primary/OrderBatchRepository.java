package com.jizhangyl.application.repository.primary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.primary.OrderBatch;



public interface OrderBatchRepository extends JpaRepository<OrderBatch, String> {
	
	
	List<OrderBatch> findByOpenId(String openId);

}
