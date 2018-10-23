package com.jizhangyl.application.task;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class EmsResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1347216272886868010L;
	private List<EmsBean> traces;

}
