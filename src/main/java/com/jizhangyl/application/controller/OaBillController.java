package com.jizhangyl.application.controller;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.primary.OaBill;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.OaBillForm;
import com.jizhangyl.application.service.OaBillService;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;


/**
 * OA
 * @author 陈栋
 * @date 2018年10月19日  
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/oa/bill")
public class OaBillController {
	
	@Autowired
    private OaBillService oaBillService;
	
	
	/**
	 * 添加账单
	 * @param modle
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/add")
	public ResultVO addBill(@Valid OaBillForm modle ,BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm = ", modle);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
		OaBill oaBill = new OaBill();
		BeanUtils.copyProperties(modle, oaBill);
		oaBillService.addOaBill(oaBill);
		return ResultVOUtil.success();
	}
    
    
	/**
	 * 根据主键搜索账单
	 * @param oaBillId
	 * @return
	 */
	@PostMapping("/details")
	public ResultVO BillDetails(@RequestParam("oaBillId") Integer oaBillId){
		OaBill oaBill = oaBillService.findByOaBillId(oaBillId);
		return ResultVOUtil.success(oaBill);
	}
	
	
	/**
	 * 根据开始结束时间查询账单列表
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@PostMapping("/list")
	public ResultVO BillList(@RequestParam("startTime") Date startTime,@RequestParam("endTime") Date endTime){
		
		List<OaBill>  list = oaBillService.findByCreateTimeBetween(startTime, endTime);
		return ResultVOUtil.success(list);
	}
    
	
	
    
    
    
    
	

}
