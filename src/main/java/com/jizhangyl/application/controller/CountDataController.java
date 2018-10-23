package com.jizhangyl.application.controller;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jizhangyl.application.enums.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dto.AllDataDTO;
import com.jizhangyl.application.dto.BuyerSalesDTO;
import com.jizhangyl.application.dto.ShopSalesDTO;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.CountDataService;
import com.jizhangyl.application.service.ExportService;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/count")
public class CountDataController {

	@Autowired
	private CountDataService countDataService;

	@Autowired
	private ExportService exportService;

	/**
	 * 处理统计数据的请求
	 * @return 全站数据,商品的销售量以及销售额,买手的销售额以及销量
	 */
	@GetMapping("/fullSiteData")
	public ResultVO countFullSiteData(String beginDate, String endDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, List<ShopSalesDTO>> shopMap = new HashMap<String, List<ShopSalesDTO>>(); // 存放商品的销售额以及销量
		Map<String, List> buyerMap = new HashMap<String, List>(); // 存放买家销售额以及订单数
		int topN = 10;
		
		// 1.查询全站的数据(货值,关税,货值+关税,运费,订单数,客单价)
		AllDataDTO allDataDTO = null;
		AllDataDTO[] allDatas = new AllDataDTO[1];

		// 2.查询每个商品的销售额以及销量
		List<ShopSalesDTO> commonShopList = null; // 存储一份公共的商品销售额以及销量数据
		List<ShopSalesDTO> shopSalesList = null;
		List<ShopSalesDTO> shopSalesNumList = null;
		// 3.查询每个买手的销售额以及订单数(已付款)
		List<BuyerSalesDTO> commonBuyerList = null; // 存储一份公共的买手销售额以及订单数
		List<BuyerSalesDTO> buyerSalesList = null;
		List<BuyerSalesDTO> buyerOrderNumsList = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (StringUtils.isEmpty(beginDate) && StringUtils.isEmpty(endDate)) { // 时间为空,则统计从今日0点到现在这一时刻的数据
			Date curDate = new Date();
			endDate = sdf.format(curDate); 						// 截止日期为:2018-09-19 11:53:22
			beginDate = endDate.substring(0, 10) + " 00:00:00"; // 开始日期为:2018-09-19 00:00:00
			// 3.1.全站数据
			allDataDTO = countDataService.countAllData(beginDate, endDate);
			allDatas[0] = allDataDTO;

			commonShopList = countDataService.countShopSales(beginDate, endDate);
			// 3.2.商品的销售额
//			shopSalesList = new ArrayList<ShopSalesDTO>(commonShopList);
//			Collections.sort(shopSalesList, new Comparator<ShopSalesDTO>(){
//				@Override
//				public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
//					if (o1.getShopSales().doubleValue() > o2.getShopSales().doubleValue()) {
//						return -1;
//					} else if (o1.getShopSales().doubleValue() < o2.getShopSales().doubleValue()) {
//						return 1;
//					}
//					return 0;
//				}
//			});
//			shopSalesList = shopSalesList.stream()
//				.limit(10).collect(Collectors.toList());
			shopSalesList = countDataService.findSalesTopN(commonShopList, topN, new Comparator<ShopSalesDTO>(){
                @Override
                public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
                    if (o1.getShopSales().doubleValue() > o2.getShopSales().doubleValue()) {
                        return 1;
                    } else if (o1.getShopSales().doubleValue() < o2.getShopSales().doubleValue()) {
                        return -1;
                    }
                    return 0;
                }
            });

			// 3.3.商品的销量
//			shopSalesNumList = new ArrayList<ShopSalesDTO>(commonShopList);
//			Collections.sort(shopSalesNumList, new Comparator<ShopSalesDTO>(){
//				@Override
//				public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
//					if (o1.getShopSalesNum().intValue() > o2.getShopSalesNum().intValue()) {
//						return -1;
//					} else if (o1.getShopSalesNum().intValue() < o2.getShopSalesNum().intValue()) {
//						return 1;
//					}
//					return 0;
//				}
//			});
//			shopSalesNumList = shopSalesNumList.stream()
//				.limit(10).collect(Collectors.toList());
			shopSalesNumList = countDataService.findSalesNumTopN(commonShopList, topN, new Comparator<ShopSalesDTO>(){
                @Override
                public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
                    if (o1.getShopSalesNum().intValue() > o2.getShopSalesNum().intValue()) {
                        return 1;
                    } else if (o1.getShopSalesNum().intValue() < o2.getShopSalesNum().intValue()) {
                        return -1;
                    }
                    return 0;
                }
            });
			
			// 3.4.买手的销售额
			commonBuyerList = countDataService.countBuyerSales(beginDate, endDate);
//			buyerSalesList = new ArrayList<BuyerSalesDTO>(commonBuyerList);
//			Collections.sort(buyerSalesList, new Comparator<BuyerSalesDTO>(){
//				@Override
//				public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
//					if (o1.getBuyerSales().doubleValue() > o2.getBuyerSales().doubleValue()) {
//						return -1;
//					} else if (o1.getBuyerSales().doubleValue() < o2.getBuyerSales().doubleValue()) {
//						return 1;
//					}
//					return 0;
//				}
//			});
//			buyerSalesList = buyerSalesList.stream()
//				.limit(10).collect(Collectors.toList());
			buyerSalesList = countDataService.findBuyerSalesTopN(commonBuyerList, topN, new Comparator<BuyerSalesDTO>(){
                @Override
                public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
                    if (o1.getBuyerSales().doubleValue() > o2.getBuyerSales().doubleValue()) {
                        return 1;
                    } else if (o1.getBuyerSales().doubleValue() < o2.getBuyerSales().doubleValue()) {
                        return -1;
                    }
                    return 0;
                }
            });
			
			// 3.5.买手的订单数
//			buyerOrderNumsList = new ArrayList<BuyerSalesDTO>(commonBuyerList);
//			Collections.sort(buyerOrderNumsList, new Comparator<BuyerSalesDTO>(){
//				@Override
//				public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
//					if (o1.getOrderNums().doubleValue() > o2.getOrderNums().doubleValue()) {
//						return -1;
//					} else if (o1.getOrderNums().doubleValue() < o2.getOrderNums().doubleValue()) {
//						return 1;
//					}
//					return 0;
//				}
//			});
//			buyerOrderNumsList = buyerOrderNumsList.stream()
//				.limit(10).collect(Collectors.toList());
			buyerOrderNumsList = countDataService.findBuyerOrderNumsTopN(commonBuyerList, topN, new Comparator<BuyerSalesDTO>(){
                @Override
                public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
                    if (o1.getOrderNums().doubleValue() > o2.getOrderNums().doubleValue()) {
                        return 1;
                    } else if (o1.getOrderNums().doubleValue() < o2.getOrderNums().doubleValue()) {
                        return -1;
                    }
                    return 0;
                }
            });
			
		} else if (!StringUtils.isEmpty(beginDate) && !StringUtils.isEmpty(endDate)) { // 时间不为空,则统计时间段内的数据
			// 注:前台需要传过来精确到时分秒的时间,例:2018-09-19 00:00:00
			// 结束时间要加一天
			endDate = DateUtil.addDay(endDate, 1);
			// 3.1.全站数据
			allDataDTO = countDataService.countAllData(beginDate, endDate);
			allDatas[0] = allDataDTO;

			commonShopList = countDataService.countShopSales(beginDate, endDate);
			// 3.2.商品的销售额
//			shopSalesList = new ArrayList<ShopSalesDTO>(commonShopList);
//			Collections.sort(shopSalesList, new Comparator<ShopSalesDTO>(){
//				@Override
//				public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
//					if (o1.getShopSales().doubleValue() > o2.getShopSales().doubleValue()) {
//						return -1;
//					} else if (o1.getShopSales().doubleValue() < o2.getShopSales().doubleValue()) {
//						return 1;
//					}
//					return 0;
//				}
//			});
//			shopSalesList = shopSalesList.stream()
//				.limit(10).collect(Collectors.toList());
			shopSalesList = countDataService.findSalesTopN(commonShopList, topN, new Comparator<ShopSalesDTO>(){
                @Override
                public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
                    if (o1.getShopSales().doubleValue() > o2.getShopSales().doubleValue()) {
                        return 1;
                    } else if (o1.getShopSales().doubleValue() < o2.getShopSales().doubleValue()) {
                        return -1;
                    }
                    return 0;
                }
            });

			// 3.3.商品的销量
//			shopSalesNumList = new ArrayList<ShopSalesDTO>(commonShopList);
//			Collections.sort(shopSalesNumList, new Comparator<ShopSalesDTO>(){
//				@Override
//				public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
//					if (o1.getShopSalesNum().intValue() > o2.getShopSalesNum().intValue()) {
//						return -1;
//					} else if (o1.getShopSalesNum().intValue() < o2.getShopSalesNum().intValue()) {
//						return 1;
//					}
//					return 0;
//				}
//			});
//			shopSalesNumList = shopSalesNumList.stream()
//				.limit(10).collect(Collectors.toList());
			shopSalesNumList = countDataService.findSalesNumTopN(commonShopList, topN, new Comparator<ShopSalesDTO>(){
                @Override
                public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
                    if (o1.getShopSalesNum().intValue() > o2.getShopSalesNum().intValue()) {
                        return 1;
                    } else if (o1.getShopSalesNum().intValue() < o2.getShopSalesNum().intValue()) {
                        return -1;
                    }
                    return 0;
                }
            });
			
			// 3.4.买手的销售额
			commonBuyerList = countDataService.countBuyerSales(beginDate, endDate);
//			buyerSalesList = new ArrayList<BuyerSalesDTO>(commonBuyerList);
//			Collections.sort(buyerSalesList, new Comparator<BuyerSalesDTO>(){
//				@Override
//				public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
//					if (o1.getBuyerSales().doubleValue() > o2.getBuyerSales().doubleValue()) {
//						return -1;
//					} else if (o1.getBuyerSales().doubleValue() < o2.getBuyerSales().doubleValue()) {
//						return 1;
//					}
//					return 0;
//				}
//			});
//			buyerSalesList = buyerSalesList.stream()
//				.limit(10).collect(Collectors.toList());
			buyerSalesList = countDataService.findBuyerSalesTopN(commonBuyerList, topN, new Comparator<BuyerSalesDTO>(){
                @Override
                public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
                    if (o1.getBuyerSales().doubleValue() > o2.getBuyerSales().doubleValue()) {
                        return 1;
                    } else if (o1.getBuyerSales().doubleValue() < o2.getBuyerSales().doubleValue()) {
                        return -1;
                    }
                    return 0;
                }
            });
			
			// 3.5.买手的订单数
//			buyerOrderNumsList = new ArrayList<BuyerSalesDTO>(commonBuyerList);
//			Collections.sort(buyerOrderNumsList, new Comparator<BuyerSalesDTO>(){
//				@Override
//				public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
//					if (o1.getOrderNums().doubleValue() > o2.getOrderNums().doubleValue()) {
//						return -1;
//					} else if (o1.getOrderNums().doubleValue() < o2.getOrderNums().doubleValue()) {
//						return 1;
//					}
//					return 0;
//				}
//			});
//			buyerOrderNumsList = buyerOrderNumsList.stream()
//				.limit(10).collect(Collectors.toList());
			buyerOrderNumsList = countDataService.findBuyerOrderNumsTopN(commonBuyerList, topN, new Comparator<BuyerSalesDTO>(){
                @Override
                public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
                    if (o1.getOrderNums().doubleValue() > o2.getOrderNums().doubleValue()) {
                        return 1;
                    } else if (o1.getOrderNums().doubleValue() < o2.getOrderNums().doubleValue()) {
                        return -1;
                    }
                    return 0;
                }
            });
			
		} else {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}

		// 商品模块
		shopMap.put("shopSales", shopSalesList);
		shopMap.put("salesNum", shopSalesNumList);

		// 买手模块
		buyerMap.put("buyerSales", buyerSalesList);
		buyerMap.put("buyerOrdersNum", buyerOrderNumsList);

		// 共三模块
		resultMap.put("allData", allDatas);
		resultMap.put("shop", shopMap);
		resultMap.put("buyer", buyerMap);

		return ResultVOUtil.success(resultMap);
	}

	/**
	 * 处理导出全站商品的请求
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return
	 */
	@GetMapping("exportAllData")
	public ResponseEntity<byte[]> exportAllData(String beginDate, String endDate) {
		List<AllDataDTO> allDataDTOList = new ArrayList<AllDataDTO>(512);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (StringUtils.isEmpty(beginDate) && StringUtils.isEmpty(endDate)) { // 导出从今天0点到当前这一时刻的那一条数据

			Date curDate = new Date();
			endDate = sdf.format(curDate); 						// 截止日期为:2018-09-19 11:53:22
			beginDate = endDate.substring(0, 10) + " 00:00:00"; // 开始日期为:2018-09-19 00:00:00
			AllDataDTO allDataDTO = countDataService.countAllData(beginDate, endDate);
			allDataDTOList.add(allDataDTO);

			return exportService.exportAllData(allDataDTOList, beginDate, endDate);
		} else if (!StringUtils.isEmpty(beginDate) && !StringUtils.isEmpty(endDate)) { // 导出指定时间段内每一天的数据(需要把每一天的数据分出来)
			endDate = DateUtil.addDay(endDate, 1); // 截止日期加一天确保开始日期和截止日期最少差一天

			String originBeginDate = beginDate;
			String originEndDate = endDate;

			/*
			开始日期:2018-08-10 00:00:00
			结束日期:2018-09-19 00:00:00

			开始日期:2018-08-10 00:00:00
			结束日期:2018-08-11 00:00:00
			*/
			AllDataDTO allDataDTO = null;
			String nextDay = DateUtil.addDay(beginDate, 1); // 计算出下一天
			while (!nextDay.equals(endDate)) {
				allDataDTO = countDataService.countAllData(beginDate, nextDay);
				allDataDTOList.add(allDataDTO);
				
				beginDate = nextDay;
				nextDay = DateUtil.addDay(beginDate, 1);
			}
			allDataDTO = countDataService.countAllData(beginDate, endDate);
			allDataDTOList.add(allDataDTO);
			
			return exportService.exportAllData(allDataDTOList, originBeginDate, originEndDate);
		} else {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
	}

	/**
	 * 处理导出商品销售额以及销量的请求
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return
	 */
	@GetMapping("exportShop")
	public ResponseEntity<byte[]> exportShopExcel(String beginDate, String endDate) {
		Map<String, List<ShopSalesDTO>> shopSalesDTOListMap = new LinkedHashMap<String, List<ShopSalesDTO>>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (StringUtils.isEmpty(beginDate) && StringUtils.isEmpty(endDate)) { // 导出从今天0点到当前这一时刻的那一批商品的销售额以及销量
			Date curDate = new Date();
			endDate = sdf.format(curDate); 						// 例:截止日期为:2018-09-19 11:53:22
			beginDate = endDate.substring(0, 10) + " 00:00:00"; // 例:开始日期为:2018-09-19 00:00:00
			List<ShopSalesDTO> shopSalesDTOList = countDataService.countShopSales(beginDate, endDate);

			// 由于查的是今天的商品销售额以及销量,所以只存这一条
			shopSalesDTOListMap.put(endDate, shopSalesDTOList);
			return exportService.exportShop(shopSalesDTOListMap, beginDate, endDate);
		} else if (!StringUtils.isEmpty(beginDate) && !StringUtils.isEmpty(endDate)) { // 导出指定时间段内每一天的数据(需要把每一天的数据分出来)
			endDate = DateUtil.addDay(endDate, 1); // 截止日期加一天确保开始日期和截止日期最少差一天

			String originBeginDate = beginDate;
			String originEndDate = endDate;

			String nextDay = DateUtil.addDay(beginDate, 1); // 计算出下一天
			while (!nextDay.equals(endDate)) {
				List<ShopSalesDTO> shopSalesDTOList = countDataService.countShopSales(beginDate, nextDay);
				shopSalesDTOListMap.put(beginDate, shopSalesDTOList);

				beginDate = nextDay;
				nextDay = DateUtil.addDay(beginDate, 1);
			}
			List<ShopSalesDTO> shopSalesDTOList = countDataService.countShopSales(beginDate, nextDay);
			shopSalesDTOListMap.put(beginDate, shopSalesDTOList);

			return exportService.exportShop(shopSalesDTOListMap, originBeginDate, originEndDate);
		} else {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
	}

	/**
	 * 处理导出买手销售额以及买手所下订单数的请求
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return
	 */
	@GetMapping("exportBuyer")
	public ResponseEntity<byte[]> exportBuyerExcel(String beginDate, String endDate) {
		Map<String, List<BuyerSalesDTO>> buyerSalesDTOListMap = new LinkedHashMap<String, List<BuyerSalesDTO>>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (StringUtils.isEmpty(beginDate) && StringUtils.isEmpty(endDate)) { // 导出从今天0点到当前这一时刻的买手的销售额以及所下订单数
			Date curDate = new Date();
			endDate = sdf.format(curDate); 						// 例:截止日期为:2018-09-19 11:53:22
			beginDate = endDate.substring(0, 10) + " 00:00:00"; // 例:开始日期为:2018-09-19 00:00:00
			List<BuyerSalesDTO> buyerSalesDTOList = countDataService.countBuyerSales(beginDate, endDate);
			
			// 由于查的是今天的商品销售额以及销量,所以只存这一条
			buyerSalesDTOListMap.put(endDate, buyerSalesDTOList);
			return exportService.exportBuyer(buyerSalesDTOListMap, beginDate, endDate);
		} else if (!StringUtils.isEmpty(beginDate) && !StringUtils.isEmpty(endDate)) { // 导出指定时间段内每一天的数据(需要把每一天的数据分出来)
			endDate = DateUtil.addDay(endDate, 1); // 截止日期加一天确保开始日期和截止日期最少差一天

			String originBeginDate = beginDate;
			String originEndDate = endDate;

			String nextDay = DateUtil.addDay(beginDate, 1); // 计算出下一天
			while (!nextDay.equals(endDate)) {
				List<BuyerSalesDTO> buyerSalesDTOList = countDataService.countBuyerSales(beginDate, nextDay);
				buyerSalesDTOListMap.put(beginDate, buyerSalesDTOList);

				beginDate = nextDay;
				nextDay = DateUtil.addDay(beginDate, 1);
			}
			List<BuyerSalesDTO> buyerSalesDTOList = countDataService.countBuyerSales(beginDate, nextDay);
			buyerSalesDTOListMap.put(beginDate, buyerSalesDTOList);

			return exportService.exportBuyer(buyerSalesDTOListMap, originBeginDate, originEndDate);
		} else {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
	}
}
