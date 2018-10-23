package com.jizhangyl.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jizhangyl.application.dataobject.Flight;
import com.jizhangyl.application.dataobject.FlightPackag;
import com.jizhangyl.application.dto.FlightDto;
import com.jizhangyl.application.dto.FlightUnfinishedDto;

/**
 * 航班流程
 * @author 陈栋
 * @date 2018年9月26日  
 * @description
 */
public interface FlightService {
	
	
	
	Page<FlightDto> findAll(Pageable pageable);
	
	
	
    /**
     * 后台列表(查询已经完成的流程)专用
     * @param packageStatus
     * @param pageable
     * @return
     */
	Page<FlightDto> findOverAll(Pageable pageable);
	
	/**
	 * 查询未完成的流程
	 * @return
	 */
	List<FlightUnfinishedDto> findAllUnfinished();
	
	/**
	 * 添加航班流程<br>
	 * @param flight
	 */
	void addFlight(Flight flight,List<String> list);
	
	/**
	 * 添加包裹
	 * @param expressNums 物流单号集合
	 * @param flightId 主键
	 * @param packageStatus 包裹流程状态
	 */
	List<String> addFlightPackag(String expressNums,Integer flightId ,Integer packageStatus);
	
	/**
	 * 根据flightId查询所有物流单号
	 * @param flightId
	 * @return
	 */
	List<FlightPackag> findAllFlightPackag(Integer flightId);
	
	
	Map<String,Object> findById(Integer flightId);
	
	

}
