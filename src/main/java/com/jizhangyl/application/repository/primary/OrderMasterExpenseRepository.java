package com.jizhangyl.application.repository.primary;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.primary.OrderMasterExpense;


public interface OrderMasterExpenseRepository extends JpaRepository<OrderMasterExpense, String>{
	
	// 按照 openid 和更新时间查询
    List<OrderMasterExpense> findByBuyerOpenidAndOrderStatusAndUpdateTimeBetween(String buyerOpenid, Integer orderStatus, Date beginDate, Date endDate);
    
    
    /**
     * 根据买手openid和订单更新时间查询订单list
     * @param buyerOpenidList
     * @param orderStatus
     * @param beginDate
     * @param endDate
     * @return
     */
    List<OrderMasterExpense> findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(List<String> buyerOpenidList, Integer orderStatus, Date beginDate, Date endDate);
    
    
    
    
    
    

}
