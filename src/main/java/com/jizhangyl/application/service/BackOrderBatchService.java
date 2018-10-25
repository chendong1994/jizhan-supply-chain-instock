package com.jizhangyl.application.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.jizhangyl.application.dataobject.primary.OrderBatch;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.dto.OrderDto;

/**
 * 后台订单批量处理
 * @author 陈栋
 * @date 2018年9月28日  
 * @description
 */
public interface BackOrderBatchService {
	
	/**
	 * 后台批量导入订单，生成微信付款二维码
	 * @param batchOrderForm
	 * @return
	 */
	Map<String,Object> addBatchOrder(List<OrderDto> list, HttpServletRequest request);
	
	/**
	 * 前端批量导入地址，写入数据库，返回list
	 * @param addrs
	 * @return
	 */
	List<WxuserAddr> getAddrs(String addrs);
	
	/**
	 * 当微信客户端扫码付款成功后，异步通知
	 * @param notifyData
	 * @return
	 */
	WxPayOrderNotifyResult notify(String notifyData);
	
	/**
	 * 根据orderBatchId,导出批量订单信息
	 * @param orderBatchId
	 * @return
	 */
	ResponseEntity<byte[]>  exportBatchOrderExcel(String orderBatchId);
	
	/**
	 * 根据openid 查询批量订单
	 * @param openId
	 * @return
	 */
	List<OrderBatch> listOrderBatchByOpenId(String openId);
	
	
	

}
