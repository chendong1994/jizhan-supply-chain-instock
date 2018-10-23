package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.Flight;
import com.jizhangyl.application.dataobject.FlightImage;
import com.jizhangyl.application.dataobject.FlightPackag;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dto.FlightDetailsDto;
import com.jizhangyl.application.dto.FlightDto;
import com.jizhangyl.application.dto.FlightUnfinishedDto;
import com.jizhangyl.application.enums.FlightImageTypeEnum;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.PackageStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.FlightImageRepository;
import com.jizhangyl.application.repository.FlightPackagRepository;
import com.jizhangyl.application.repository.FlightRepository;
import com.jizhangyl.application.repository.OrderMasterRepository;
import com.jizhangyl.application.service.FlightService;
import com.jizhangyl.application.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FlightServiceImpl implements FlightService{
	
	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private FlightPackagRepository flightPackagRepository;
	@Autowired
	private OrderMasterRepository orderMasterRepository;
	@Autowired
	private FlightImageRepository flightImageRepository;
	
	

	@Override
	public Page<FlightDto> findAll(Pageable pageable) {
		List<FlightDto> list = new ArrayList<FlightDto>();
		Page<Flight> pg = flightRepository.findAll(pageable);
		if(CollectionUtils.isNotEmpty(pg.getContent())){
			for(Flight vv : pg.getContent()){
				FlightDto flightDto = new FlightDto();
				BeanUtils.copyProperties(vv, flightDto);
				flightDto.setCreateTime(DateUtil.dateToString(vv.getCreateTime()));
				flightDto.setFlightGoTime(DateUtil.dateToString(vv.getFlightGoTime()));
				list.add(flightDto);
			}
		}
		return new PageImpl<>(list, pageable, pg.getTotalElements());
	}
	
	
	@Override
	public List<FlightUnfinishedDto> findAllUnfinished() {
		List<FlightUnfinishedDto> list = new ArrayList<FlightUnfinishedDto>();
		Pageable pageRequest = new PageRequest(0, 100);
		Page<Flight> pg= flightRepository.findByStatus(2, pageRequest);
		if(CollectionUtils.isNotEmpty(pg.getContent())){
			for(Flight vv : pg.getContent()){
				FlightUnfinishedDto flightUnfinishedDto = new FlightUnfinishedDto();
				BeanUtils.copyProperties(vv, flightUnfinishedDto);
				list.add(flightUnfinishedDto);
			}
		}
		
		return list;
	}
	
	

	@Override
	public Page<FlightDto> findOverAll( Pageable pageable) {
		List<FlightDto> list = new ArrayList<FlightDto>();
		
		Page<Flight> pg= flightRepository.findByStatus(1, pageable);
		if(CollectionUtils.isNotEmpty(pg.getContent())){
			for(Flight flight : pg.getContent()){
				FlightDto flightDto = new FlightDto();
				flightDto.setCreateTime(DateUtil.dateToString(flight.getCreateTime()));
				flightDto.setFlightGoTime(DateUtil.dateToString(flight.getFlightGoTime()));
				flightDto.setDeliveryNumber(flight.getDeliveryNumber());
				flightDto.setVoyage(flight.getVoyage());
				flightDto.setPackageNumber(flight.getPackageNumber());
				flightDto.setFlightWeight(flight.getFlightWeight());
				flightDto.setProportion(flight.getProportion());
				flightDto.setPackAllWeight(flight.getPackAllWeight());
				
				list.add(flightDto);
			}
		}
		return new PageImpl<>(list, pageable, pg.getTotalElements());
	}

	@Override
	@Transactional
	public void addFlight(Flight flight,List<String> list) {
		if(flight == null){
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
		//航班预定
		if(flight.getPackageStatus() == PackageStatusEnum.FLIGHT_RESERVATION.getCode()){
			if(flight.getFlightId() != null){
				Flight fv = flightRepository.findOne(flight.getFlightId());
				if(fv!=null){
					fv.setVoyage(flight.getVoyage());
					fv.setFlightGoTime(flight.getFlightGoTime());
					fv.setFlightArriveTime(flight.getFlightArriveTime());
					//判断提货单号是否存在
					Flight fb = flightRepository.findByDeliveryNumber(flight.getDeliveryNumber());
					if(fb != null){
						if(!fb.getFlightId().equals(fv.getFlightId())){
							throw new GlobalException(ResultEnum.DELIVERYNUMBER_IS_EXIST); 
						}
					}
					//修改提运单号
					List<FlightPackag> lis = flightPackagRepository.findByDeliveryNumber(fv.getDeliveryNumber());
					if(CollectionUtils.isNotEmpty(lis)){
						for(FlightPackag flightPackag : lis){
							flightPackag.setDeliveryNumber(flight.getDeliveryNumber());
						}
						flightPackagRepository.save(lis);
					}
					fv.setDeliveryNumber(flight.getDeliveryNumber());
					flightRepository.save(fv);
				}
			}
			else{
				//验证提运单号唯一性
				Flight fl = flightRepository.findByDeliveryNumber(flight.getDeliveryNumber());
				if(fl != null){
					throw new GlobalException(ResultEnum.DELIVERYNUMBER_IS_EXIST); 
				}
				flight.setStatus(2);
				flight.setCustomsClearanceStatus(2);
				flight.setFlightArrivalStatus(2);
				flight.setFlightChargeStatus(2);
				flight.setPackageToAirportStatus(2);
				flight.setPackPackageStatus(2);
				flightRepository.save(flight);
			}
		}
		//包裹打包
		if(flight.getPackageStatus() == PackageStatusEnum.PACK_PACKAGE.getCode()){
			// TODO 
		}
		//包裹送机
		if(flight.getPackageStatus() == PackageStatusEnum.PACKAGE_TO_AIRPORT.getCode()){
			Flight flightp =  flightRepository.findOne(flight.getFlightId());
			if(flightp == null){
				throw new GlobalException(ResultEnum.PARAM_EMPTY); 
			}
			flightp.setDeliveryFlightCost(flight.getDeliveryFlightCost());
			flightp.setDeliveryFlightOther(flight.getDeliveryFlightOther());
			flightp.setDeliveryFlightOtherCost(flight.getDeliveryFlightOtherCost());
			if(flightp.getPackageStatus() < flight.getPackageStatus()){
				flightp.setPackageStatus(flight.getPackageStatus());
			}
			flightp.setPackageToAirportStatus(1);
			flightRepository.save(flightp);
			
			saveImages(flight.getFlightId(),list,FlightImageTypeEnum.DELIVERY_FLIGHT_URL);
		}
		//收航空费
		if(flight.getPackageStatus() == PackageStatusEnum.FLIGHT_CHARGE.getCode()){
			Flight flightp =  flightRepository.findOne(flight.getFlightId());
			if(flightp == null){
				throw new GlobalException(ResultEnum.PARAM_EMPTY); 
			}
			flightp.setFlightVolume(flight.getFlightVolume());
			flightp.setFlightWeight(flight.getFlightWeight());
			flightp.setFlightCost(flight.getFlightCost());
			flightp.setFlightOthe(flight.getFlightOthe());
			flightp.setFlightOtherCost(flight.getFlightOtherCost());
			if(flightp.getPackageStatus() < flight.getPackageStatus()){
				flightp.setPackageStatus(flight.getPackageStatus());
			}
			flightp.setFlightChargeStatus(1);
			//根据提运单号查询所有用户物流单号
			List<FlightPackag> listfl = flightPackagRepository.findByDeliveryNumber(flightp.getDeliveryNumber());
			List<String> listExpressNumber = new ArrayList<String>(); 
			if(CollectionUtils.isNotEmpty(listfl)){
				for(FlightPackag v : listfl){
					listExpressNumber.add(v.getExpNum());
				}
			}
			List<OrderMaster> listo = orderMasterRepository.findByExpressNumberIn(listExpressNumber);
			BigDecimal weinght =  BigDecimal.ZERO;//订单付款包裹重量
			if(CollectionUtils.isNotEmpty(listo)){
				for(OrderMaster orderMaster : listo){
					//运费金额除每公斤价格
					BigDecimal bh = orderMaster.getOrderFreight().divide(new BigDecimal(35),10,BigDecimal.ROUND_HALF_DOWN);
					weinght = weinght.add(bh);
				}
			}
			flightp.setPackAllWeight(weinght);
			if(weinght.compareTo(BigDecimal.ZERO) != 0 && flight.getFlightWeight() != null){
				flightp.setProportion(new BigDecimal(1).subtract(flight.getFlightWeight().divide(weinght,10,BigDecimal.ROUND_HALF_DOWN)));
			}
			
			flightRepository.save(flightp);
			
			saveImages(flight.getFlightId(),list,FlightImageTypeEnum.FLIGHT_URL);
		}
		//航班到港
		if(flight.getPackageStatus() == PackageStatusEnum.FLIGHT_ARRIVAL.getCode()){
			Flight flightp =  flightRepository.findOne(flight.getFlightId());
			if(flightp == null){
				throw new GlobalException(ResultEnum.PARAM_EMPTY); 
			}
			if(flightp.getPackageStatus() < flight.getPackageStatus()){
				flightp.setPackageStatus(flight.getPackageStatus());
			}
			flightp.setFlightArrivalStatus(1);//到港
			flightRepository.save(flightp);
			
		}
		//清关完毕
		if(flight.getPackageStatus() == PackageStatusEnum.CUSTOMS_CLEARANCE.getCode()){
			Flight flightp =  flightRepository.findOne(flight.getFlightId());
			if(flightp == null){
				throw new GlobalException(ResultEnum.PARAM_EMPTY); 
			}
			flightp.setCustomsWeight(flight.getCustomsWeight());
			flightp.setCustomsPoll(flight.getCustomsPoll());
			flightp.setCustomsGroundCost(flight.getCustomsGroundCost());
			flightp.setCustomsCost(flight.getCustomsCost());
			flightp.setCustomsTransactionCost(flight.getCustomsTransactionCost());
			flightp.setCustomsBorderCost(flight.getCustomsBorderCost());
			flightp.setCustomsEmsCost(flight.getCustomsEmsCost());
			flightp.setCustomsShippingCost(flight.getCustomsShippingCost());
			flightp.setCustomsOther(flight.getCustomsOther());
			flightp.setCustomsOtherCost(flight.getCustomsOtherCost());
			flightp.setPackageStatus(flight.getPackageStatus());
			if( flightp.getFlightArrivalStatus()==1 && flightp.getFlightChargeStatus()
					==1 && flightp.getPackageToAirportStatus()==1 && flightp.getPackPackageStatus()==1){
				flightp.setStatus(1);
			}
			flightp.setCustomsClearanceStatus(1);
			flightRepository.save(flightp);
			
			saveImages(flight.getFlightId(),list,FlightImageTypeEnum.CUSTOMS_URL);
		}
		
	}


    

	@Override
	@Transactional
	public List<String> addFlightPackag(String expressNums,Integer flightId ,Integer packageStatus) {
		//查询主键是否存在
		Flight flight = flightRepository.findOne(flightId);
		if(flight == null){
			throw new GlobalException(ResultEnum.PARAM_EMPTY); 
		}
		
		//报错物流单号
		List<String> listMsg= new ArrayList<String>();
		String[] enumss = expressNums.split(" ");
		
		List<String> resultList = new ArrayList<>(Arrays.asList(enumss));
		for  ( int  i  =   0 ; i  <  resultList.size()  -   1 ; i ++ )  {       
            for  ( int  j  =  resultList.size()  -   1 ; j  >  i; j -- )  {       
                 if  (resultList.get(j).equals(resultList.get(i)))  { 
                	 listMsg.add(resultList.get(j)+"==重复的物流单号");
                	 resultList.remove(j);
                  }        
             }        
        }  
		//去除空格
		List<String> newList = new ArrayList<String>();
		for(int i = 0 ; i < resultList.size() ; i ++){
			newList.add(resultList.get(i));
		}
		//要添加的物流单号
		for(int i = 0 ; i < newList.size() ; i ++){
			//判断长度
			if(newList.get(i).length() != 13){
				listMsg.add(newList.get(i)+"==物流单号长度不正确");
				newList.remove(i);
			}
		}
		// 数据库所有重复的物流单号
		List<FlightPackag> listdball = flightPackagRepository.findByExpNumIn(newList);
		List<String> listb = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(listdball)){
			for(FlightPackag flightPackag1 : listdball){
				listb.add(flightPackag1.getExpNum());
				listMsg.add(flightPackag1.getExpNum()+"==数据库已经存在此物流单号");
			}
			newList.removeAll(listb);
		}
		//组装数据
		List<FlightPackag> listf = new ArrayList<FlightPackag>();
		for(String bb : newList){
			FlightPackag flightPackag = new FlightPackag();
			flightPackag.setExpNum(bb);
			flightPackag.setDeliveryNumber(flight.getDeliveryNumber());
			listf.add(flightPackag);
		}
		flightPackagRepository.save(listf);

		//修改航班表包裹数量
		if(flight.getPackageNumber() != null){
			flight.setPackageNumber(flight.getPackageNumber()+listf.size());
		}else{
			flight.setPackageNumber(listf.size());
		}
		flight.setPackPackageStatus(1);
		flight.setPackageStatus(PackageStatusEnum.PACK_PACKAGE.getCode());
		//根据提运单号查询所有用户物流单号
		List<OrderMaster> listo = orderMasterRepository.findByExpressNumberIn(newList);
		BigDecimal weinght =  BigDecimal.ZERO;
		for(OrderMaster orderMaster : listo){
			weinght = weinght.add(orderMaster.getPackWeight());
		}
		flight.setPackAllWeight(weinght);
		if(weinght.compareTo(BigDecimal.ZERO) != 0 && flight.getFlightWeight() != null){
			flight.setProportion(new BigDecimal(1).subtract(flight.getFlightWeight().divide(weinght,10,BigDecimal.ROUND_HALF_DOWN)));
		}
		flightRepository.save(flight);
		
		//修改orderMaster表数据
		List<OrderMaster> orderList = orderMasterRepository.findByExpressNumberInAndOrderStatusNot(newList, OrderStatusEnum.CANCELED.getCode());
		if(CollectionUtils.isNotEmpty(orderList)){
			for(OrderMaster orderMaster : orderList){
				orderMaster.setDeliveryNumber(flight.getDeliveryNumber());
				orderMaster.setVoyage(flight.getVoyage());
				orderMaster.setExpectedArrivalTime(flight.getFlightArriveTime());
			}
		}
		
		orderMasterRepository.save(orderList);
		//返回错误信息
		return listMsg;
		
	}

	@Override
	public List<FlightPackag> findAllFlightPackag(Integer flightId) {
		Flight flight = flightRepository.findOne(flightId);
		if(flight == null){
			throw new GlobalException(ResultEnum.PARAM_EMPTY); 
		}
		List<FlightPackag>  list = flightPackagRepository.findByDeliveryNumber(flight.getDeliveryNumber());
		return list;
	}



	@Override
	public Map<String,Object> findById(Integer flightId) {
		Map<String,Object> map = new HashMap<String,Object>();
		Flight flight = flightRepository.findOne(flightId);
		
		FlightDetailsDto flightDetailsDto = new FlightDetailsDto();
		BeanUtils.copyProperties(flight, flightDetailsDto);
		flightDetailsDto.setCreateTime(DateUtil.dateToString(flight.getCreateTime()));
		flightDetailsDto.setFlightArriveTime(DateUtil.dateToString(flight.getFlightArriveTime()));
		map.put("flight", flightDetailsDto);
		
		List<FlightImage> list1 = flightImageRepository.findByFlightIdAndType(flightId, FlightImageTypeEnum.DELIVERY_FLIGHT_URL.getCode());
		List<FlightImage> list2 = flightImageRepository.findByFlightIdAndType(flightId, FlightImageTypeEnum.FLIGHT_URL.getCode());
		List<FlightImage> list3 = flightImageRepository.findByFlightIdAndType(flightId, FlightImageTypeEnum.CUSTOMS_URL.getCode());
		
		map.put("deliveryFlightUrl", list1);
		map.put("flightUrl", list2);
		map.put("customsUrl", list3);
		
		return map;
	}
	
	
	//============================private=========================
	
	/**
     * 保存图片
     * @param flightId
     * @param list
     * @param type
     */
	private void saveImages(Integer flightId, List<String> list,FlightImageTypeEnum type) {
		List<FlightImage> listImage = new ArrayList<FlightImage>();
		for(String imageUrl : list){
			FlightImage flightImage = new FlightImage();
			flightImage.setFlightId(flightId);
			flightImage.setType(type.getCode());
			flightImage.setUrl(imageUrl);
			listImage.add(flightImage);
		}
		flightImageRepository.save(listImage);
	}




	
	

}
