package com.jizhangyl.application.repository.primary;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jizhangyl.application.dataobject.primary.OrderMaster;

/**
 * @author 杨贤达
 * @date 2018/7/26 21:32
 * @description
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster, String> {

    /**
     * 根据订单id查询订单
     * @param orderId
     * @return
     */
    List<OrderMaster> findByOrderId(String orderId);
    
    List<OrderMaster> findByBuyerOpenidAndOrderStatusOrderByUpdateTimeDesc(String openid, Integer orderStatus);

    List<OrderMaster> findByBuyerOpenidAndOrderStatusOrderByCreateTimeDesc(String openid, Integer orderStatus);
    
    Page<OrderMaster> findByBuyerOpenidAndOrderStatusInOrderByCreateTimeDesc(String openid,
                                                                             List<Integer> orderStatusList, Pageable pageable);

    /**
     * 按照 openid 和时间范围查询销售额
     * @param openid
     * @param beginDate
     * @param endDate
     * @return
     */
    /*@Query(value = "select new OrderMaster(orderCost, orderTaxes, orderFreight) from orderMaster where buyerOpenid = ?1 and createTime between ?2 and ?3",
    nativeQuery = true)*/
    @Query(value = "select * from order_master where buyer_openid = ?1 and create_time between ?2 and ?3",
    nativeQuery = true)
    List<OrderMaster> findByBuyerOpenid(String openid, Date beginDate, Date endDate);

    Page<OrderMaster> findByBuyerOpenid(String openid, Pageable pageable);

    List<OrderMaster> findByOrderIdIn(List<String> orderIdList);
    
    List<OrderMaster> findByOrderStatusAndDeliveryTimeBetween(Integer orderStatus, Date startTime, Date endTime);

    List<OrderMaster> findByExpressNumber(String expressNumber);

    OrderMaster findByExpressNumberAndOrderStatusNotIn(String expressNumber, List<Integer> orderStatusList);

    List<OrderMaster> findByCreateTimeBetween(Date startTime, Date endTime);

    List<OrderMaster> findByExpressNumberIn(List<String> expressNumberList);

    /**
     * 统计全站的货值之和
     * @return 全站的货值之和
     */
    @Query(value = "select sum(order_cost) cost from order_master where pay_status = 1",
    nativeQuery = true)
    BigDecimal countGPrice();

    /**
     * 统计指定时间段内的全站的货值之和
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 指定时间段内的全站的货值之和
     */
    @Query(value = "select sum(order_cost) cost from order_master where pay_status = 1 and create_time between ?1 and ?2",
    		nativeQuery = true)
    BigDecimal countGPrice(Date beginDate, Date endDate);

    /**
     * 统计全站的关税之和
     * @return 全站的关税之和
     */
    @Query(value = "select sum(order_taxes) taxes from order_master where pay_status = 1",
    		nativeQuery = true)
    BigDecimal countTaxes();

    /**
     * 统计指定时间段内的全站的关税之和
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 指定时间段内的全站的关税之和
     */
    @Query(value = "select sum(order_taxes) taxes from order_master where pay_status = 1 and create_time between ?1 and ?2",
    		nativeQuery = true)
    BigDecimal countTaxes(Date beginDate, Date endDate);

    /**
     * 统计全站的运费之和
     * @return 全站的运费之和
     */
    @Query(value = "select sum(order_freight) taxes from order_master where pay_status = 1",
    		nativeQuery = true)
    BigDecimal countFreight();

    /**
     * 统计指定时间段内的全站的运费之和
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 指定时间段内的全站的运费之和
     */
    @Query(value = "select sum(order_freight) taxes from order_master where pay_status = 1 and create_time between ?1 and ?2",
    		nativeQuery = true)
    BigDecimal countFreight(Date beginDate, Date endDate);

    /**
     * 统计全站的订单数之和
     * @return 全站的订单数之和
     */
    @Query(value = "select count(*) orders from order_master where pay_status = 1",
    		nativeQuery = true)
    Integer countOrders();

    /**
     * 统计指定时间段内的全站的订单数之和
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 指定时间段内的全站的订单数之和
     */
    @Query(value = "select count(*) orders from order_master where pay_status = 1 and create_time between ?1 and ?2",
    		nativeQuery = true)
    Integer countOrders(Date beginDate, Date endDate);

    OrderMaster findByExpressNumberAndOrderStatus(String expressNumber, Integer orderStatus);

    List<OrderMaster> findByBuyerOrderIdIn(List<String> buyerOrderIdList);

    // 按照 openid 和更新时间查询
    List<OrderMaster> findByBuyerOpenidAndOrderStatusAndUpdateTimeBetween(String buyerOpenid, Integer orderStatus, Date beginDate, Date endDate);
    
    /**
     * 查询订单状态不为已收货的单子
     * @param orderStatusList
     * @return
     */
    List<OrderMaster> findByOrderStatusIn( List<Integer> orderStatusList);

    List<OrderMaster> findByOrderStatus(Integer orderStatus);

    List<OrderMaster> findByOrderStatusOrderByLastNotifyTime(Integer orderStatus);
    
    List<OrderMaster> findByExpressNumberInAndOrderStatusNot(List<String> expressNumberList,Integer orderStatus);
    
    /**
     * 根据订单状态已经开始日期和截止日期查询
     * @param orderStatus
     * @param startTime
     * @param endTime
     * @return
     */
    List<OrderMaster> findByOrderStatusAndCreateTimeBetween(Integer orderStatus, Date startTime, Date endTime);
}
