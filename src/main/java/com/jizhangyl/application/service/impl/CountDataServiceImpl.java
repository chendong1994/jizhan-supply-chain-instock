package com.jizhangyl.application.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import com.jizhangyl.application.enums.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dataobject.Shop;
import com.jizhangyl.application.dataobject.Wxuser;
import com.jizhangyl.application.dto.AllDataDTO;
import com.jizhangyl.application.dto.BuyerSalesDTO;
import com.jizhangyl.application.dto.ShopSalesDTO;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.OrderDetailRepository;
import com.jizhangyl.application.repository.OrderMasterRepository;
import com.jizhangyl.application.repository.ShopRepository;
import com.jizhangyl.application.repository.WxuserRepository;
import com.jizhangyl.application.service.CountDataService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年9月19日 上午9:34:00
 * @description 负责统计数据
 */
@Slf4j
@Service
public class CountDataServiceImpl implements CountDataService {

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private WxuserRepository wxuserRepository;

    /**
     * 统计某一时间段内的全站数据(时间段限定在一天范围内)
     * 
     * @param beginDate
     *            开始日期
     * @param endDate
     *            截止日期
     * @return 某一时间段内的全站数据
     */
    @Override
    public AllDataDTO countAllData(String beginDate, String endDate) {
        AllDataDTO allDataDTO = new AllDataDTO();

        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfExcel = new SimpleDateFormat("yyyy.MM.dd");
        // 1.查询出所有的订单数据
        List<OrderMaster> orderMasterList = orderMasterRepository.findAll();
        try {
            Date beginTime = sdf.parse(beginDate);
            Date endTime = sdf.parse(endDate);

            // 2.筛选出已付款并且在指定时间段以内的订单==>(筛选出除了未付款和"已经取消"的所有订单)
            orderMasterList = orderMasterList.stream().filter(e -> {
                if (e.getOrderStatus().intValue() != OrderStatusEnum.NEW.getCode().intValue()
                        && e.getOrderStatus().intValue() != OrderStatusEnum.CANCELED.getCode().intValue()
                        && e.getOrderStatus().intValue() != OrderStatusEnum.FIND_ALL.getCode().intValue()
                        && e.getCreateTime().getTime() >= beginTime.getTime()
                        && e.getCreateTime().getTime() <= endTime.getTime()) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            // 3. 筛选出在指定时间段之内的订单
            // 上面已经完成合并

            // 4.计算出这些订单的总货值
            BigDecimal costSum = new BigDecimal(0);
            // 5.计算出这些订单的总关税
            BigDecimal taxesSum = new BigDecimal(0);
            // 7.计算出这些订单的总运费
            BigDecimal freightSum = new BigDecimal(0);
            for (OrderMaster orderMaster : orderMasterList) {

                BigDecimal subCost = orderMaster.getOrderCost();
                costSum = costSum.add(subCost);

                BigDecimal subTaxes = orderMaster.getOrderTaxes();
                taxesSum = taxesSum.add(subTaxes);

                BigDecimal subFreight = orderMaster.getOrderFreight();
                freightSum = freightSum.add(subFreight);
            }

            // 6.计算出这些订单的总货值+总关税
            BigDecimal costAndTaxesSum = costSum.add(taxesSum);

            // 8.计算出这些订单的总订单数
            Integer totalOrders = orderMasterList.size();

            // 9.计算出这些订单的总客单价((总货值+关税+总运费)/总订单数)
            // ps:需要考虑总订单数为0的情况
            BigDecimal customerPrice = new BigDecimal(0);
            if (totalOrders.intValue() > 0) {
                customerPrice = costAndTaxesSum.add(freightSum);
                customerPrice = customerPrice.divide(new BigDecimal(totalOrders.intValue()), 2);
            }

            allDataDTO.setCost(costSum);
            allDataDTO.setTaxes(taxesSum);
            allDataDTO.setCostAndTaxes(costAndTaxesSum);
            allDataDTO.setFreight(freightSum);
            allDataDTO.setOrderNums(totalOrders);
            allDataDTO.setCustomerPrice(customerPrice);
            allDataDTO.setDate(sdfExcel.format(beginTime)); // 只取开始日期的年月日部分(因为时间段被限定在一天之内),日期格式:2018.09.18
        } catch (ParseException e) {
            throw new GlobalException(ResultEnum.QUERY_DATE_FORMAT_ERROR);
        }
        return allDataDTO;
    }

    /**
     * 统计某一时间段内的所有商品的"销量"以及"销售额"(时间段限定在一天范围内)
     * 
     * @param beginDate
     *            开始日期
     * @param endDate
     *            截止日期
     * @return 某一时间段内的所有商品的"销量"以及"销售额"
     */
    @Override
    public List<ShopSalesDTO> countShopSales(String beginDate, String endDate) {
        List<ShopSalesDTO> shopSalesDTOList = new ArrayList<ShopSalesDTO>();
        // 1.查询出商品表中的全部商品
        List<Shop> shopList = shopRepository.findAll();
        // 将商品id以及它对应的商品对象放到一个map中存储
        Map<Integer, Shop> shopMap = new HashMap<Integer, Shop>();
        for (int i = 0; i < shopList.size(); i++) {
            Shop shop = shopList.get(i);
            shopMap.put(shop.getId(), shop);
        }

        // 2.筛选出已付款的主订单==>(筛选出除了未付款的和已经"取消"的所有订单)
        List<OrderMaster> orderMasterList = orderMasterRepository.findAll();
        orderMasterList = orderMasterList.stream().filter(e -> {
            if (e.getOrderStatus().intValue() != OrderStatusEnum.NEW.getCode().intValue()
                    && e.getOrderStatus().intValue() != OrderStatusEnum.CANCELED.getCode().intValue()
                    && e.getOrderStatus().intValue() != OrderStatusEnum.FIND_ALL.getCode().intValue()) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        // 3.筛选出已付款的主订单下的订单明细
        List<OrderDetail> orderDetailList = orderDetailRepository.findAll();
        List<OrderDetail> filterDetailList = new ArrayList<OrderDetail>(1024); // 用于存储一次筛选的结果
        for (int i = 0; i < orderMasterList.size(); i++) {
            OrderMaster orderMaster = orderMasterList.get(i);
            for (int j = 0; j < orderDetailList.size(); j++) {
                OrderDetail orderDetail = orderDetailList.get(j);
                if (orderDetail.getOrderId().equals(orderMaster.getOrderId())) {
                    filterDetailList.add(orderDetail);
                }
            }
        }

        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfExcel = new SimpleDateFormat("yyyy.MM.dd");
        try {
            Date beginTime = sdf.parse(beginDate);
            Date endTime = sdf.parse(endDate);
            // 4.从筛选出的订单明细列表中再次筛选出在指定的时间段以内的订单
            filterDetailList = filterDetailList.stream().filter(e -> {
                if (e.getCreateTime().getTime() >= beginTime.getTime()
                        && e.getCreateTime().getTime() <= endTime.getTime()) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            // 5.求出每个商品的销售额,销量,库存
            Map<Integer, ShopSalesDTO> shopSalesMap = new HashMap<Integer, ShopSalesDTO>();
            for (int i = 0; i < filterDetailList.size(); i++) {
                OrderDetail orderDetail = filterDetailList.get(i);
                if (shopSalesMap.containsKey(Integer.valueOf(orderDetail.getProductId()))) {
                    ShopSalesDTO shopSalesDTO = shopSalesMap.get(Integer.valueOf(orderDetail.getProductId()));

                    // 5.1.更新商品的销售额
                    BigDecimal saleMoney = orderDetail.getProductPrice(); // 商品价格
                    BigDecimal tax = orderDetail.getDetailTaxes(); // 商品税收
                    saleMoney = saleMoney.add(tax); // 货值+关税
                    BigDecimal productQuantity = BigDecimal.valueOf(orderDetail.getProductQuantity().intValue()); // 商品数量
                    saleMoney = saleMoney.multiply(productQuantity); // 该商品的销售额
                    // 累加此处销售额
                    saleMoney = saleMoney.add(shopSalesDTO.getShopSales());
                    shopSalesDTO.setShopSales(saleMoney);

                    // 5.2.更新商品的销量
                    Integer oldSalesNum = shopSalesDTO.getShopSalesNum();
                    shopSalesDTO.setShopSalesNum(orderDetail.getProductQuantity() + oldSalesNum);
                } else {
                    Shop shop = shopMap.get(Integer.valueOf(orderDetail.getProductId()));

                    ShopSalesDTO shopSalesDTO = new ShopSalesDTO();
                    // 5.1.设置日期
                    shopSalesDTO.setDate(sdfExcel.format(beginTime));

                    // 5.2.设置商品的jancode
                    shopSalesDTO.setShopJan(shop.getShopJan());

                    // 5.3.设置商品名称
                    shopSalesDTO.setShopName(shop.getShopName());

                    // 5.4.设置销售额(商品价格+商品税收)*商品数量
                    BigDecimal saleMoney = orderDetail.getProductPrice(); // 商品价格
                    BigDecimal tax = orderDetail.getDetailTaxes(); // 商品税收
                    saleMoney = saleMoney.add(tax); // 货值+关税
                    BigDecimal productQuantity = BigDecimal.valueOf(orderDetail.getProductQuantity().intValue()); // 商品数量
                    saleMoney = saleMoney.multiply(productQuantity); // 该商品的销售额
                    shopSalesDTO.setShopSales(saleMoney);

                    // 5.5.设置销量
                    shopSalesDTO.setShopSalesNum(orderDetail.getProductQuantity());

                    // 5.6.设置库存
                    shopSalesDTO.setInventory(shop.getShopCount());

                    shopSalesMap.put(Integer.valueOf(orderDetail.getProductId()), shopSalesDTO);
                }
            }
            // 6.将满足条件的商品的销售额放到ShopSalesDTO中存储
            Collection<ShopSalesDTO> shopSlaesDTOList = shopSalesMap.values();
            shopSalesDTOList = new ArrayList<ShopSalesDTO>(shopSlaesDTOList);
        } catch (ParseException e) {
            throw new GlobalException(ResultEnum.QUERY_DATE_FORMAT_ERROR);
        } catch (NumberFormatException e) {
            log.info("【商品ID格式化错误】");
            e.printStackTrace();
        }
        return shopSalesDTOList;
    }

    /**
     * 统计某一时间段内的所有买手的"销售额"以及所下"订单数"(时间段限定在一天范围内)
     * 
     * @param beginDate
     *            开始日期
     * @param endDate
     *            截止日期
     * @return 某一时间段内的所有买手的"销售额"以及所下"订单数"
     */
    @Override
    public List<BuyerSalesDTO> countBuyerSales(String beginDate, String endDate) {
        List<BuyerSalesDTO> buyerSalesDTOList = null;
        // 1.查询出所有的买手
        List<Wxuser> wxuserList = wxuserRepository.findAll();
        // 将买手openid以及它对应的买手对象放到一个map中存储
        Map<String, Wxuser> wxuserMap = new HashMap<String, Wxuser>();
        for (int i = 0; i < wxuserList.size(); i++) {
            Wxuser wxuser = wxuserList.get(i);
            wxuserMap.put(wxuser.getOpenId(), wxuser);
        }

        // 2.筛选出已付款的主订单==>(筛选出除了未付款的所有订单)(还得在指定时间段以内)=======================>见277行
        List<OrderMaster> orderMasterList = orderMasterRepository.findAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfExcel = new SimpleDateFormat("yyyy.MM.dd");
        try {
            // 3.从筛选出的主订单中再次筛选出在指定的时间段以内的订单
            Date beginTime = sdf.parse(beginDate);
            Date endTime = sdf.parse(endDate);
            orderMasterList = orderMasterList.stream() // 在此处同时筛选出了在指定时间段之内的订单以及除了未付款的和以外的所有订单
                    .filter(e -> {
                        if (e.getOrderStatus().intValue() != OrderStatusEnum.NEW.getCode().intValue()
                                && e.getOrderStatus().intValue() != OrderStatusEnum.CANCELED.getCode().intValue()
                                && e.getOrderStatus().intValue() != OrderStatusEnum.FIND_ALL.getCode().intValue()
                                && e.getCreateTime().getTime() >= beginTime.getTime()
                                && e.getCreateTime().getTime() <= endTime.getTime()) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());

            // 4.统计出每一个openid以及对应"销售额","订单量","买手下游人数"
            Map<String, BuyerSalesDTO> buyerSalesMap = new HashMap<String, BuyerSalesDTO>();
            for (int i = 0; i < orderMasterList.size(); i++) {
                OrderMaster orderMaster = orderMasterList.get(i);
                if (buyerSalesMap.containsKey(orderMaster.getBuyerOpenid())) { // openId已经计算过
                    BuyerSalesDTO buyerSalesDTO = buyerSalesMap.get(orderMaster.getBuyerOpenid());

                    // 4.1.更新销售额(货值+关税)
                    BigDecimal originBuySales = buyerSalesDTO.getBuyerSales();
                    buyerSalesDTO.setBuyerSales(
                            orderMaster.getOrderCost().add(orderMaster.getOrderTaxes()).add(originBuySales));

                    // 4.2.更新订单量
                    Integer originOrderNums = buyerSalesDTO.getOrderNums();
                    buyerSalesDTO.setOrderNums(originOrderNums + 1);

                } else { // openId没有计算过
                    Wxuser wxuser = wxuserMap.get(orderMaster.getBuyerOpenid());

                    BuyerSalesDTO buyerSalesDTO = new BuyerSalesDTO();
                    // 4.1.设置日期
                    buyerSalesDTO.setDate(sdfExcel.format(beginTime));

                    // 4.2.设置买手邀请码
                    buyerSalesDTO.setInviteCode(wxuser.getInviteCode());

                    // 4.3.设置买手姓名
                    buyerSalesDTO.setBuyerName(wxuser.getNickName());

                    // 4.4.设置销售额(货值+关税)
                    buyerSalesDTO.setBuyerSales(orderMaster.getOrderCost().add(orderMaster.getOrderTaxes()));

                    // 4.5.设置订单量
                    buyerSalesDTO.setOrderNums(1);

                    // 4.6.设置买手下游人数
                    Integer downStreamCount = wxuserRepository.findDownStreamCount(wxuser.getInviteCode());
                    buyerSalesDTO.setDownStreamCount(downStreamCount);

                    buyerSalesMap.put(orderMaster.getBuyerOpenid(), buyerSalesDTO);
                }
            }

            // 5.将map中的买手的销售额以及所下的订单数的DTO对象取出来
            Collection<BuyerSalesDTO> coll = buyerSalesMap.values();
            buyerSalesDTOList = new ArrayList<BuyerSalesDTO>(coll);
        } catch (ParseException e) {
            throw new GlobalException(ResultEnum.QUERY_DATE_FORMAT_ERROR);
        }

        return buyerSalesDTOList;
    }
    
    /**
     * 优化商品"销售额"排序
     */
    @Override
    public List<ShopSalesDTO> findSalesTopN(List<ShopSalesDTO> input, int n, Comparator<ShopSalesDTO> comparator) {
        if (input == null || input.size() == 0 || n < 1 || comparator == null) {
            log.info("【findSalesTopN方法出现空指针异常】");
            throw new GlobalException();
        }
        
        // 小根堆的大小与传入的n相等
        // 注意:如果是对象类型的需要传入比较器或者实现Comparable接口
        PriorityQueue<ShopSalesDTO> minHeap = new PriorityQueue<ShopSalesDTO>(n, comparator);
        // 1.优先级队列内部为堆结构,默认小根堆,此处时间复杂度O(nlog(n))
        for (int i = 0; i < input.size(); i++) {
            if (minHeap.size() < n) {
                minHeap.offer(input.get(i));
            } else if (minHeap.peek().getShopSales().doubleValue() < input.get(i).getShopSales().doubleValue()) {
                minHeap.poll();
                minHeap.offer(input.get(i));
            }
        }
        List<ShopSalesDTO> results = new ArrayList<ShopSalesDTO>(minHeap);
        // 只对这n个数排序,性能要远高于对所有的元素排序
        // 2.此处因为n的值一般很小,所以复杂度记为O(1) => 总复杂度O(nlog(n))
        Collections.sort(results, comparator.reversed());
        return results;
    }

    /**
     * 优化商品"销量"排序
     */
    @Override
    public List<ShopSalesDTO> findSalesNumTopN(List<ShopSalesDTO> input, int n, Comparator<ShopSalesDTO> comparator) {
        if (input == null || input.size() == 0 || n < 1 || comparator == null) {
            log.info("【findSalesNumTopN方法出现空指针异常】");
            throw new GlobalException();
        }
        
        // 小根堆的大小与传入的n相等
        // 注意:如果是对象类型的需要传入比较器或者实现Comparable接口
        PriorityQueue<ShopSalesDTO> minHeap = new PriorityQueue<ShopSalesDTO>(n, comparator);
        // 1.优先级队列内部为堆结构,默认小根堆,此处时间复杂度O(nlog(n))
        for (int i = 0; i < input.size(); i++) {
            if (minHeap.size() < n) {
                minHeap.offer(input.get(i));
            } else if (minHeap.peek().getShopSalesNum().intValue() < input.get(i).getShopSalesNum().intValue()) {
                minHeap.poll();
                minHeap.offer(input.get(i));
            }
        }
        List<ShopSalesDTO> results = new ArrayList<ShopSalesDTO>(minHeap);
        // 只对这n个数排序,性能要远高于对所有的元素排序
        // 2.此处因为n的值一般很小,所以复杂度记为O(1) => 总复杂度O(nlog(n))
        Collections.sort(results, comparator.reversed());
        return results;
    }

    /**
     * 优化"买手销售额"排序
     */
    @Override
    public List<BuyerSalesDTO> findBuyerSalesTopN(List<BuyerSalesDTO> input, int n, Comparator<BuyerSalesDTO> comparator) {
        if (input == null || input.size() == 0 || n < 1 || comparator == null) {
            log.info("【findBuyerSalesTopN方法出现空指针异常】");
            throw new GlobalException();
        }
        
        // 小根堆的大小与传入的n相等
        // 注意:如果是对象类型的需要传入比较器或者实现Comparable接口
        PriorityQueue<BuyerSalesDTO> minHeap = new PriorityQueue<BuyerSalesDTO>(n, comparator);
        // 1.优先级队列内部为堆结构,默认小根堆,此处时间复杂度O(nlog(n))
        for (int i = 0; i < input.size(); i++) {
            if (minHeap.size() < n) {
                minHeap.offer(input.get(i));
            } else if (minHeap.peek().getBuyerSales().doubleValue() < input.get(i).getBuyerSales().doubleValue()) {
                minHeap.poll();
                minHeap.offer(input.get(i));
            }
        }
        List<BuyerSalesDTO> results = new ArrayList<BuyerSalesDTO>(minHeap);
        // 只对这n个数排序,性能要远高于对所有的元素排序
        // 2.此处因为n的值一般很小,所以复杂度记为O(1) => 总复杂度O(nlog(n))
        Collections.sort(results, comparator.reversed());
        return results;
    }

    /**
     * 优化"买手订单数"排序
     */
    @Override
    public List<BuyerSalesDTO> findBuyerOrderNumsTopN(List<BuyerSalesDTO> input, int n, Comparator<BuyerSalesDTO> comparator) {
        if (input == null || input.size() == 0 || n < 1 || comparator == null) {
            log.info("【findBuyerOrderNumsTopN 方法出现空指针异常】");
            throw new GlobalException();
        }
        
        // 小根堆的大小与传入的n相等
        // 注意:如果是对象类型的需要传入比较器或者实现Comparable接口
        PriorityQueue<BuyerSalesDTO> minHeap = new PriorityQueue<BuyerSalesDTO>(n, comparator);
        // 1.优先级队列内部为堆结构,默认小根堆,此处时间复杂度O(nlog(n))
        for (int i = 0; i < input.size(); i++) {
            if (minHeap.size() < n) {
                minHeap.offer(input.get(i));
            } else if (minHeap.peek().getOrderNums().doubleValue() < input.get(i).getOrderNums().doubleValue()) {
                minHeap.poll();
                minHeap.offer(input.get(i));
            }
        }
        List<BuyerSalesDTO> results = new ArrayList<BuyerSalesDTO>(minHeap);
        // 只对这n个数排序,性能要远高于对所有的元素排序
        // 2.此处因为n的值一般很小,所以复杂度记为O(1) => 总复杂度O(nlog(n))
        Collections.sort(results, comparator.reversed());
        return results;
    }
    
}
