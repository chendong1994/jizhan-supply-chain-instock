package com.jizhangyl.application.repository.primary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.primary.Flight;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
	
	
	Page<Flight> findByPackageStatus(Integer packageStatus, Pageable pageable);
	
	
	Flight findByDeliveryNumber(String deliveryNumber);
	
	
	Page<Flight> findByStatus(Integer status,Pageable pageable);
	
	

}
