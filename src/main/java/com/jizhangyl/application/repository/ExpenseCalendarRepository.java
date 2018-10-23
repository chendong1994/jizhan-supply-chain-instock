package com.jizhangyl.application.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.jizhangyl.application.dataobject.ExpenseCalendar;

/**
 * 
 * @author 陈栋
 * @date 2018年9月17日  
 * @description 
 */
public interface ExpenseCalendarRepository  extends JpaRepository<ExpenseCalendar, Integer>,JpaSpecificationExecutor<ExpenseCalendar>{
	
	/**
	 * 根据用户微信openid和月份查询那个月的销售情况
	 * @param openId
	 * @param monthTime
	 * @return
	 */
	ExpenseCalendar findByOpenIdAndMonthTime(String openId,String monthTime);
	
	/**
	 * 查询某个用户哪年的销售情况
	 * @param openId
	 * @param monthTime
	 * @return
	 */
	List<ExpenseCalendar> findByOpenIdAndMonthTimeLike(String openId , String monthTime);
	
	
	List<ExpenseCalendar> findByOpenId(String openId );
	
	Page<ExpenseCalendar> findByOrderByExpenseCalendarIdAsc(Pageable pageable);
	
	
	List<ExpenseCalendar> findByMonthTime(String monthTime);
	
	Page<ExpenseCalendar> findByMonthTimeOrderByCreateTimeDesc(String monthTime,Pageable pageable);
	
	Page<ExpenseCalendar> findByInviteCodeOrUserNameOrderByCreateTimeDesc(String inviteCode, String userName,Pageable pageable);
	
	
	Page<ExpenseCalendar> findByParentInviteCode(String parentInviteCode,Pageable pageable);
	
	Page<ExpenseCalendar> findByInviteCodeLikeOrUserNameLikeAndMonthTimeOrderByCreateTimeDesc(String inviteCode, String userName,String monthTime,Pageable pageable);

}
