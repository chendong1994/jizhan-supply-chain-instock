package com.jizhangyl.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.OrderBatchRelation;

public interface OrderBatchRelationRepository extends JpaRepository<OrderBatchRelation, String> {
	
	
	List<OrderBatchRelation> findByOrderBatchId(String orderBatchId);
	

}
