package com.jizhangyl.application.repository.primary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.primary.OrderBatchRelation;

public interface OrderBatchRelationRepository extends JpaRepository<OrderBatchRelation, String> {
	
	
	List<OrderBatchRelation> findByOrderBatchId(String orderBatchId);
	

}
