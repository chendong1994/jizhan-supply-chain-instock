package com.jizhangyl.application.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.jizhangyl.application.dataobject.OrderForRepository;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dto.AllDataDTO;
import com.jizhangyl.application.dto.BuyerSalesDTO;
import com.jizhangyl.application.dto.ShopSalesDTO;

/**
 * @author 曲健磊
 * @date 2018年9月18日 上午11:08:00
 * @description 负责导出商品的销售额以及商品的销量,买手的销售额以及买手所下订单数的服务层接口
 * 导出订单管理模块的订单excel
 */
public interface ExportService {

	/**
	 * 导出全站的数据
	 * @param allDataDTOList 全站数据的list集合
	 * @param startTime 开始日期
	 * @param endTime 截止日期
	 * @return excel文件
	 */
	public ResponseEntity<byte[]> exportAllData(List<AllDataDTO> allDataDTOList, String startTime, String endTime);

	/**
	 * 导出商品的销售额以及销量
	 * @param shopSalesDTOListMap 存储了时间段范围内的每一天商品的销售额以及销量的map集合
	 * key为日期格式的字符串"2018-09-19",value为对应那天的所有商品的销售额以及销量
	 * @param startTime 开始日期
	 * @param endTime 截止日期
	 * @return excel文件
	 */
	public ResponseEntity<byte[]> exportShop(Map<String, List<ShopSalesDTO>> shopSalesDTOListMap, String startTime, String endTime);

	/**
	 * 导出买手的销售额以及所下订单数
	 * @param buyerSalesDTOListMap 存储了时间段范围内的每一天买手的销售额以及所下订单数的map集合
	 * key为日期格式的字符串"2018-09-19",value为对应那天的所有买手的销售额以及所下订单数
	 * @param startTime 开始日期
	 * @param endTime 截止日期
	 * @return excel文件
	 */
	public ResponseEntity<byte[]> exportBuyer(Map<String, List<BuyerSalesDTO>> buyerSalesDTOListMap, String startTime, String endTime);
	
	/**
	 * 导出指定的订单列表
	 * @param orderForRepositoryList 待导出的订单列表
	 * @param startTime 开始时间
	 * @param endTime 截止时间
	 * @return excel文件
	 */
	public ResponseEntity<byte[]> exportOrder(List<OrderForRepository> orderForRepositoryList, Date startTime, Date endTime);
}
