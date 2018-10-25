package com.jizhangyl.application.repository.primary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.primary.FlightImage;

public interface FlightImageRepository extends JpaRepository<FlightImage, Integer>{
	
	List<FlightImage> findByFlightId(Integer flightId);
	
	
	List<FlightImage> findByFlightIdAndType(Integer flightId,Integer type);
	

}
