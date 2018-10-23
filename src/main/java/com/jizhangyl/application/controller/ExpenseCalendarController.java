package com.jizhangyl.application.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jizhangyl.application.VO.ExpenseCalendarVO;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.ExpenseCalendar;
import com.jizhangyl.application.dataobject.Wxuser;
import com.jizhangyl.application.service.ExpenseCalendarService;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户销售情况（下游返点，权益等）
 * @author 陈栋
 * @date 2018年9月19日  
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/expense")
public class ExpenseCalendarController {
	
	@Autowired
    private ExpenseCalendarService expenseCalendarService ;
	
    
	/**
	 * 手动调用方法计算上个月每个用户的下游返点情况<br>
	 * 慎用！！！<br>
	 * 慎用！！！<br>
	 * 慎用！！！<br>
	 * 当定时任务不生效时，可手动调用此方法
	 * 
	 * @return
	 */
	@GetMapping("/calculate")
	public void calculate(){
		expenseCalendarService.calculate();
	}
	
	/**
	 * 后台分页查询
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/list")
	@ResponseBody
	public ResultVO getAllExpense(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                  @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
                                  @RequestParam(value = "userName") String userName,
                                  @RequestParam(value = "monthTime") String monthTime){
		Page<ExpenseCalendar> expenseCalendarPage = null;
		PageRequest pageRequest = new PageRequest(page - 1, size);
		if(StringUtils.isBlank(monthTime)){
			expenseCalendarPage = expenseCalendarService.findByAllNow(userName, userName, pageRequest);
		}else{
		    expenseCalendarPage = expenseCalendarService.findByAll(userName,userName,monthTime,pageRequest);
		}
		
		List<ExpenseCalendarVO> expenseCalendarVOList = expenseCalendarPage.getContent().stream().map(e -> {
			ExpenseCalendarVO expenseCalendarVO = new ExpenseCalendarVO();
            BeanUtils.copyProperties(e, expenseCalendarVO);
            return expenseCalendarVO;
        }).collect(Collectors.toList()); 
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", expenseCalendarVOList);
		map.put("totalPage", expenseCalendarPage.getTotalPages());
		map.put("totalNum", expenseCalendarPage.getTotalElements());
		return ResultVOUtil.success(map);
	}
	
	
	/**
	 * 查询当月返点详情
	 * @param openid
	 * @return
	 */
	@GetMapping("/details")
	@ResponseBody
	public ResultVO detailsByOpenid(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
						            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
						            @RequestParam("expenseCalendarId")Integer expenseCalendarId,
						            @RequestParam("openid")String openid){
		Map<String,Object> map = new HashMap<String,Object>();
		PageRequest pageRequest = new PageRequest(page - 1, size);
		if(expenseCalendarId != null){
			map = expenseCalendarService.detailsByExpenseCalendarId(expenseCalendarId,pageRequest);
		}else{
			map = expenseCalendarService.detailsByOpenid(openid,pageRequest);
		}
		
		return ResultVOUtil.success(map);
	}
	
	/**
	 * 设置白名单：0关闭，1开启
	 * 
	 * @param openid
	 * @param status
	 * @return
	 */
	@GetMapping("/setStatus")
    public ResultVO setExpensewWhiteStatus(@RequestParam("openid") String openid,@RequestParam("status") Integer status) {
    	expenseCalendarService.updateExpensewWhiteStatus(openid, status);
    	return ResultVOUtil.success();
    }
	
	/**
	 * 用户权益等级列表
	 * @return
	 */
	@GetMapping("/user/list")
	public ResultVO userList() {
		List<Wxuser>  list = expenseCalendarService.userList();
    	return ResultVOUtil.success(list);
    }
	
	
	/**
	 * 导出excel
	 * @param dateStr
	 * @return
	 */
	@GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(@RequestParam("date") String dateStr) {
    	if(StringUtils.isBlank(dateStr)){
    		Calendar endDate = Calendar.getInstance();
    		endDate.setTime(new Date());
    		endDate.add(Calendar.MONTH, 0); // 月份减
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
    		dateStr  = formatter.format(endDate.getTime());
    	}
    	return expenseCalendarService.exportExcel(dateStr);
    }
	

}
