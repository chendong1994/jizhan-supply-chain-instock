package com.jizhangyl.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.FlightPackag;

public interface FlightPackagRepository extends JpaRepository<FlightPackag, Integer>{
	
	
	List<FlightPackag> findByDeliveryNumber(String deliveryNumber);
	
	List<FlightPackag> findByExpNumIn(List<String>  expNumList);

}
