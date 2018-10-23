package com.jizhangyl.application.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.OrderForm;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨贤达
 * @date 2018/8/12 19:07
 * @description OrderForm2OrderDto
 */
@Slf4j
public class OrderForm2OrderDto {

    /**
     * orderForm -> orderDto
     * @param orderForm
     * @return orderDto
     */
    public static OrderDto convert(OrderForm orderForm) {
        Gson gson = new Gson();
        OrderDto orderDto = new OrderDto();
        orderDto.setBuyerOpenid(orderForm.getOpenid());
        orderDto.setBuyerAddrId(orderForm.getSenderAddrId());
        orderDto.setRecipientAddrId(orderForm.getReceiverAddrId());

        List<OrderDetail> orderDetailList = new ArrayList<>();
        try {
            orderDetailList = gson.fromJson(orderForm.getItems(), new TypeToken<List<OrderDetail>>() {
            }.getType());
        } catch (Exception e) {
            log.error("【对象转换】错误, string = {}", orderForm.getItems());
            throw new GlobalException(ResultEnum.PARAM_ERROR);
        }
        orderDto.setOrderDetailList(orderDetailList);
        return orderDto;
    }
    
    /**
     * list对象转换，把错误的和成功的分开
     * @param list
     * @return
     */
    public static List<OrderDto> convertList(List<OrderForm> list) {
    	Gson gson = new Gson();
    	List<OrderDto> listOrderDto = new ArrayList<>();//转成功的list
    	for(OrderForm orderForm : list){
    		OrderDto orderDto = new OrderDto();
    		orderDto.setBuyerOpenid(orderForm.getOpenid());
    		orderDto.setBuyerAddrId(orderForm.getSenderAddrId());
    		orderDto.setRecipientAddrId(orderForm.getReceiverAddrId());
    		
    		List<OrderDetail> orderDetailList = new ArrayList<>();
    		try {
    			orderDetailList = gson.fromJson(orderForm.getItems(), new TypeToken<List<OrderDetail>>() {
    			}.getType());
    		} catch (Exception e) {
    			log.error("【对象转换】错误, string = {}", orderForm.getItems());
    			throw new GlobalException(ResultEnum.PARAM_ERROR);
    		}
    		orderDto.setOrderDetailList(orderDetailList);
    		listOrderDto.add(orderDto);
    	}
        return listOrderDto;
    	
    }
    
    
    
    
    
}
