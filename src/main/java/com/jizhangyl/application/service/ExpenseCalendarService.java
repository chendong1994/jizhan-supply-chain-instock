package com.jizhangyl.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.jizhangyl.application.dataobject.primary.ExpenseCalendar;
import com.jizhangyl.application.dataobject.primary.Interests;
import com.jizhangyl.application.dataobject.secondary.Wxuser;

/**
 * 
 * @author 陈栋
 * @date 2018年9月17日  
 * @description 用户每月返点收益情况
 */
public interface ExpenseCalendarService {
	
	/**
	 * 计算每个用户上个月的销售额和返点金额，定时任务专用
	 */
	void calculate();
	
	/**
	 * 分页查询,用来给后台查询列表用<br>
	 * 根据月份,用户名,邀请码查询
	 * @return
	 */
	Page<ExpenseCalendar> findByAll(String inviteCode,String userName,String monthTime,Pageable pageable);
	
	/**
	 * 根据用户名和邀请码分页查询当前月用户返点情况
	 * @param inviteCode
	 * @param userName
	 * @param pageable
	 * @return
	 */
	Page<ExpenseCalendar> findByAllNow(String inviteCode,String userName,Pageable pageable);
	
	
	
	/**
	 * 查询某个用户哪年的销售情况(如果monthTime为空，那就查询此用户所有时间的销售情况)
	 * @param openId
	 * @param monthTime
	 * @return
	 */
	List<ExpenseCalendar> findByOpenIdAndMonthTimeLike(String openId , String monthTime);
	
	/**
	 * 根据用户微信openid和月份查询那个月的销售情况
	 * @param openId
	 * @param monthTime
	 * @return
	 */
	ExpenseCalendar findByOpenIdAndMonthTime(String openId,String monthTime);
	
	
	/**
	 * 查询用户本月的返点金额
	 * @param openId
	 * @return
	 */
	BigDecimal getNowExpenseSum(String openId);
	
	/**
	 * 后台修改权益详情
	 * @param interestsId
	 */
	void  updateInteger(Interests interests);
		
		
	/**
	 * 如果物流收获，改变订单状态为已收货,复制表order_master到表order_master_expense<br>
	 * 定时任务专用<br>
	 * 不做数据库 回滚处理。<br>
	 * 慎用	！！！
	 */
	void  updateOrderStatus();
	
	
	
	
	/**
	 * 每隔15分钟取消订单(定时任务专用)
	 */
	void cancelOrder();
	
	/**
	 * 导出excel<br>
	 * 没选择月份，导出上个月数据
	 * @param time
	 * @return
	 */
	ResponseEntity<byte[]>  exportExcel(String time);
	
	
	/**
	 * 查询当月用户实时下游返点详情
	 * @param openid
	 * @return
	 */
	Map<String,Object> detailsByOpenid(String openid,PageRequest pageRequest);


	/**
	 * 查询当月用户实时下游返点详情
	 * @param unionId
	 * @return
	 */
	Map<String,Object> detailsByUnionId(String unionId,PageRequest pageRequest);

	/**
	 * 根据主键查询某月下游用户的返点情况
	 * @return
	 */
	Map<String, Object> detailsByExpenseCalendarId(Integer expenseCalendarId,PageRequest pageRequest);
	
	/**
	 * 设置开启关闭用户白名单
	 * @param openid
	 */
	void updateExpensewWhiteStatus(String openid,Integer status);
	
	/**
	 * 返回用户已经激活列表
	 * @return
	 */
	List<Wxuser> userList();
	
	
	
	
	
	
	

}
