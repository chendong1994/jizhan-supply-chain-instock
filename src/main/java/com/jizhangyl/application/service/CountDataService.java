package com.jizhangyl.application.service;

import java.util.Comparator;
import java.util.List;

import com.jizhangyl.application.dto.AllDataDTO;
import com.jizhangyl.application.dto.BuyerSalesDTO;
import com.jizhangyl.application.dto.ShopSalesDTO;

/**
 * @author 曲健磊
 * @date 2018年9月18日 下午11:32:01
 * @description 处理对数据报表的相关操作
 */
public interface CountDataService {

	/**
	 * 统计某一时间段内的全站数据(时间段限定在一天范围内)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 某一时间段内的全站数据
	 */
	AllDataDTO countAllData(String beginDate, String endDate);

	/**
	 * 统计某一时间段内的所有商品的"销量"以及"销售额"(时间段限定在一天范围内)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 某一时间段内的所有商品的"销量"以及"销售额"
	 */
	List<ShopSalesDTO> countShopSales(String beginDate, String endDate);
	
	/**
	 * 统计某一时间段内的所有买手的"销售额"以及所下"订单数"(时间段限定在一天范围内)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 某一时间段内的所有买手的"销售额"以及所下"订单数"
	 */
	List<BuyerSalesDTO> countBuyerSales(String beginDate, String endDate);
	
	/**
     * 优化商品"销售额"排序
     */
	List<ShopSalesDTO> findSalesTopN(List<ShopSalesDTO> input, int n, Comparator<ShopSalesDTO> comparator);
	
	/**
     * 优化商品"销量"排序
     */
	List<ShopSalesDTO> findSalesNumTopN(List<ShopSalesDTO> input, int n, Comparator<ShopSalesDTO> comparator);
	
	/**
	 * 优化"买手销售额"排序
	 */
	List<BuyerSalesDTO> findBuyerSalesTopN(List<BuyerSalesDTO> input, int n, Comparator<BuyerSalesDTO> comparator);
	
	/**
	 * 优化"买手订单数"排序
	 */
	List<BuyerSalesDTO> findBuyerOrderNumsTopN(List<BuyerSalesDTO> input, int n, Comparator<BuyerSalesDTO> comparator);
}
