package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.converter.OrderForm2OrderDto;
import com.jizhangyl.application.dataobject.primary.OrderBatch;
import com.jizhangyl.application.dataobject.secondary.UserInfo;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.BatchOrderForm;
import com.jizhangyl.application.form.OrderForm;
import com.jizhangyl.application.service.BackOrderBatchService;
import com.jizhangyl.application.service.UserInfoService;
import com.jizhangyl.application.service.WxuserService;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户模块
 * @author 陈栋
 * @date 2018年9月28日  
 * @description
 */
@Slf4j
@Controller
@RequestMapping("/back")
public class BackController {
	
	
	@Autowired
	private BackOrderBatchService backOrderBatchService;
	@Autowired
	private WxuserService wxuserService;
	@Autowired
	private UserInfoService userInfoService;
	
	/**
	 * 前端3个字段，3列。在第四列加个;，用来做分割符。<br>
	 * 批量添加收货人地址，返回地址详情list(前段根据对象里面的主键id来组装要买的商品数据,返给后台)
	 * @param addrs
	 * @return
	 */
	@ResponseBody
	@PostMapping("/addAddrs")
	public ResultVO getAddrs(@RequestParam("addrs")String addrs){
		//1.处理传入的字符串
		StringBuffer addrs3 = new StringBuffer();
		addrs = addrs.replace("\t", "").replaceAll(" ", "").replace(",", "").replace("，", "");//去除大空格,英文逗号，中文逗号
		String[] fgb = addrs.split("=");
		for(String bb : fgb){
			if(bb.length()>0){
				addrs3.append(bb+"\\n");
			}
		}
		//2.调用api，得到地址集合，写入数据库
		List<WxuserAddr> list = backOrderBatchService.getAddrs(addrs3.toString());
		return ResultVOUtil.success(list);
	}
	
	
	/**
	 * 批量处理后台导入订单
	 * @param modle
	 * @param request
	 * @return
	 */
	@ResponseBody
	@PostMapping("/batchPay")
	public ResultVO batchOrder(@RequestBody BatchOrderForm modle, HttpServletRequest request){
		if (CollectionUtils.isEmpty(modle.getList())) {
			throw new GlobalException(ResultEnum.PARAM_ERROR);
		}
		
		//  根据后台openID找到小程序openID，利用unioid,替换openID
		UserInfo userInfo = userInfoService.findByOpenid(modle.getList().get(0).getOpenid());
		Wxuser wxuser = wxuserService.findByUnionId(userInfo.getUnionid());
		for(OrderForm orderForm : modle.getList()){
			orderForm.setOpenid(wxuser.getOpenId());
		}
		
		//1.重组数据
		List<OrderDto> list = OrderForm2OrderDto.convertList(modle.getList());
		if (CollectionUtils.isEmpty(list)) {
            log.error("【创建订单】购物车不能为空");
            throw new GlobalException(ResultEnum.CART_EMPTY);
        }
		
		//2.创建订单
		Map<String,Object>  mapr = backOrderBatchService.addBatchOrder(list,request);
		Map<String,Object> mapResult = new HashMap<String,Object>();
		
		mapResult.put("url", mapr.get("url"));
		
		return ResultVOUtil.success(mapResult);
		
		
		
	}
	
	
	 /**
     * 微信异步通知
     * （微信收款成功回调此接口通知我们）
     * @param notifyData
     * @return
     */
    @ResponseBody
    @PostMapping("/pay/notify")
    public String notify(@RequestBody String notifyData) {
    	
    	backOrderBatchService.notify(notifyData);

        // 返回给微信处理结果
        /*
        <xml>
            <return_code><![CDATA[SUCCESS]]></return_code>
            <return_msg><![CDATA[OK]]></return_msg>
        </xml>
         */
        String xmlResult = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
        return xmlResult;
    }
    
    /**
     * 导出Excel
     * @param dateStr
     * @return
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(@RequestParam("orderBatchId") String orderBatchId) {
    	return backOrderBatchService.exportBatchOrderExcel(orderBatchId);
    }
	
    /**
     * 根据小程序用户openid 查询历史批量订单
     * @param openId
     * @return
     */
    @GetMapping("/batchorder/list")
    public ResultVO listOrderBatchByOpenId(@RequestParam("openId") String openId){
    	List<OrderBatch>  list = backOrderBatchService.listOrderBatchByOpenId(openId);
    	return ResultVOUtil.success(list);
    }
    
    
	
	
    

}
