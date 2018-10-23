package com.jizhangyl.application.form;

import java.io.Serializable;
import java.util.List;

import lombok.Data;


@Data
public class BatchOrderForm implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8121945385420056926L;
	
	
	private List<OrderForm> list; 
	

}
