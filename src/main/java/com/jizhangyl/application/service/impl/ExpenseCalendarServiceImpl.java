package com.jizhangyl.application.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.dataobject.ExpenseCalendar;
import com.jizhangyl.application.dataobject.Interests;
import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dataobject.OrderMasterExpense;
import com.jizhangyl.application.dataobject.Wxuser;
import com.jizhangyl.application.dto.ExpenseCalendarDto;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.enums.ExpensewWhiteStatusEnum;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.enums.UserGardeEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.ExpenseCalendarRepository;
import com.jizhangyl.application.repository.InterestsRepository;
import com.jizhangyl.application.repository.OrderDetailRepository;
import com.jizhangyl.application.repository.OrderMasterExpenseRepository;
import com.jizhangyl.application.repository.OrderMasterRepository;
import com.jizhangyl.application.repository.WxuserRepository;
import com.jizhangyl.application.service.ExpenseCalendarService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.task.EmsBean;
import com.jizhangyl.application.task.EmsResponse;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.EMSUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExpenseCalendarServiceImpl implements ExpenseCalendarService {

	@Autowired
	private ExpenseCalendarRepository expenseCalendarRepository;
	@Autowired
	private WxuserRepository wxuserRepository;
	@Autowired
	private OrderMasterRepository orderMasterRepository;
	@Autowired
	private InterestsRepository interestsRepository;
	@Autowired
	private EMSUtil eMSUtil;
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	@Autowired
	private ShopService shopService;
	@Autowired
	private OrderMasterExpenseRepository orderMasterExpenseRepository;
	

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void calculate() {
		// 1.查询激活用户信息
		List<Wxuser> listWU = wxuserRepository.findByInviteCodeNotNull();
		if (listWU != null && !listWU.isEmpty()) {
			List<ExpenseCalendar> listExpenseCalendar = new ArrayList<ExpenseCalendar>();
			
			for (Wxuser wu : listWU) {
				// 2.查询order_master，用来计算每个用户每个月的销售金额
				// ----这个月的收获金额减去这个月的退货金额，这个退货额先暂时不做,只计算每月收货情况下的订单----
				List<OrderMasterExpense> listOM = orderMasterExpenseRepository.findByBuyerOpenidAndOrderStatusAndUpdateTimeBetween(
						wu.getOpenId(), OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(-1),
						DateUtil.getSupportEndDayofMonth(-1));

				// 用户自身每月销售金额
				BigDecimal useramount = BigDecimal.ZERO;
				if (listOM != null && !listOM.isEmpty()) {
					for (OrderMasterExpense om : listOM) {
						useramount = useramount.add(om.getOrderCost());// 用货值计算
					}
				}
				ExpenseCalendar expenseCalendar = new ExpenseCalendar();
				expenseCalendar.setOpenId(wu.getOpenId());// 用户openID
				expenseCalendar.setUserGrade(wu.getUserGrade());// 权益等级
				expenseCalendar.setExpenseSum(useramount);// 用户自己每月销售金额
				expenseCalendar.setInviteCode(wu.getInviteCode());// 邀请码
				expenseCalendar.setParentInviteCode(wu.getParentInviteCode());// 上级邀请码
				expenseCalendar.setJoinTime(wu.getCreateTime());// 加入时间
				expenseCalendar.setUserName(wu.getNickName());
				expenseCalendar.setAvatarUrl(wu.getAvatarUrl());

				// 3.根据用户当前权益查询权益等级详情表
				Interests interests = interestsRepository.findByUserGrade(wu.getUserGrade());
				expenseCalendar.setSalesDistribution(interests.getSalesDistribution());
				expenseCalendar.setExpressDiscount(interests.getExpressDiscount());
				expenseCalendar.setProductDiscount(interests.getProductDiscount());
				expenseCalendar.setIndirectSalesDistribution(interests.getIndirectSalesDistribution());
				expenseCalendar.setMonthTime(DateUtil.getFrontMonth());

				// 4.用户下游用户返点情况
				List<Wxuser> listw = wxuserRepository.findByParentInviteCodeAndUserGradeNotAndCreateTimeBetween(wu.getInviteCode(),UserGardeEnum.ZERO.getCode(),
						DateUtil.getSupportBeginDayofMonth(-1200), DateUtil.getSupportEndDayofMonth(-1));

				Map<String, Object> map = countDownDistribution(wu, listw);

				int pn = (Integer) map.get("pn");
				expenseCalendar.setDownstreamPeople(pn);// 本月新增下游人数
				BigDecimal amountAll = (BigDecimal) map.get("amountAll");
				expenseCalendar.setDownstreamSum(amountAll);// 下游用户每月消费金额
				expenseCalendar.setSalesDistributionSum(amountAll.multiply(interests.getSalesDistribution()));// 下游用户每月返点金额

				// 5.计算间接下游返点情况
				int indirectDownstreamPeople = 0;
				BigDecimal indirectSalesDistributionSum = BigDecimal.ZERO;
				if (listw != null && listw.size() > 0) {
					List<String> parentInviteCodeList = new ArrayList<String>();
					for (Wxuser wxuserd : listw) {
						parentInviteCodeList.add(wxuserd.getInviteCode());
					}
					//间接下游人员
					List<Wxuser> listIndirect = wxuserRepository.findByParentInviteCodeInAndUserGradeNotAndCreateTimeBetween(
							parentInviteCodeList,UserGardeEnum.ZERO.getCode(), DateUtil.getSupportBeginDayofMonth(-1200),DateUtil.getSupportEndDayofMonth(-1));
					if(listIndirect != null && listIndirect.size()>0){
						indirectDownstreamPeople = listIndirect.size();
						
						List<String> pilist = new ArrayList<String>();
						for(Wxuser wxuser : listIndirect){
							pilist.add(wxuser.getOpenId());
						}
						List<OrderMasterExpense> listOrderE = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(pilist,
								OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(-1),DateUtil.getSupportEndDayofMonth(-1));
						if(listOrderE != null && listOrderE.size() >0){
							for(OrderMasterExpense orderMasterExpense : listOrderE){
								indirectSalesDistributionSum = indirectSalesDistributionSum.add(orderMasterExpense.getOrderCost());
							}
						}
					}
				}
				expenseCalendar.setIndirectDownstreamPeople(indirectDownstreamPeople);// 间接下游每月发展人数
				expenseCalendar.setIndirectDownstreamSum(indirectSalesDistributionSum);// 间接下游没有销售总和
				expenseCalendar.setIndirectSalesDistributionSum(indirectSalesDistributionSum.multiply(interests.getIndirectSalesDistribution()));// 间接下游每月返点金额

				listExpenseCalendar.add(expenseCalendar);

				// 2.修改wxuser表的用户权益等级
				int usergarde = 0;
				usergarde = decideUserGarde(useramount, pn);
				
				//判断白名单
				if(wu.getExpenseWhiteStatus().equals(ExpensewWhiteStatusEnum.CLOSE.getCode())){
					// 判断用户权益等级是上升还是下降
					if (usergarde < wu.getUserGrade()) {
						usergarde = wu.getUserGrade() - 1;
					}
					wu.setUserGrade(usergarde);
					
				}else{
					if(usergarde > wu.getUserGrade()){
						wu.setUserGrade(usergarde);
					}
				}
				
			}
			expenseCalendarRepository.save(listExpenseCalendar);
			wxuserRepository.save(listWU);
		}
	}

	/**
	 * 计算用户一级下游某月人员和销售金额
	 * 
	 * @param wu  用户
	 * @param listw 所有下游人员（等级不为0的）
	 * @return
	 */
	private Map<String, Object> countDownDistribution(Wxuser wu, List<Wxuser> listw) {
		Map<String, Object> map = new HashMap<String, Object>();
		BigDecimal amountAll = BigDecimal.ZERO;
		// 1.遍历下游所有用户的上个月销售金额
		if (listw != null && listw.size() > 0) {
			List<String> list1 = new ArrayList<String>();
			for (Wxuser wxuser : listw) {
				list1.add(wxuser.getOpenId());
			}
			List<OrderMasterExpense> listOM2 = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(
					list1, OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(-1),DateUtil.getSupportEndDayofMonth(-1));

			if (listOM2 != null && !listOM2.isEmpty()) {
				for (OrderMasterExpense om : listOM2) {
					amountAll = amountAll.add(om.getOrderCost());
				}
			}
		}
		map.put("amountAll", amountAll);
		// 2.计算用户上个月底为止的下线人数(不为等级0的)
		List<Wxuser> listuserx = wxuserRepository.findByParentInviteCodeAndUserGradeNotAndCreateTimeBetween(wu.getInviteCode(),UserGardeEnum.ZERO.getCode(),
				DateUtil.getSupportBeginDayofMonth(-1200), DateUtil.getSupportEndDayofMonth(-1));
		map.put("pn", listuserx.size());
		return map;
	}
	
	

	@Override
	public Page<ExpenseCalendar> findByAll(String inviteCode,String userName, String monthTime, Pageable pageable) {
		// 规格定义
//		Specification<ExpenseCalendar> specification = new Specification<ExpenseCalendar>() {
//			@Override
//			public Predicate toPredicate(Root<ExpenseCalendar> root, CriteriaQuery<?> criteriaQuery,
//					CriteriaBuilder cb) {
//				List<Predicate> list = new ArrayList<Predicate>();
//				if (StringUtils.isNotBlank(inviteCode)) {
//					list.add(cb.equal(root.get("inviteCode").as(String.class),  inviteCode));
//				}
//				if (StringUtils.isNotBlank(monthTime)) {
//					list.add(cb.like(root.get("monthTime").as(String.class), "%" + monthTime + "%"));
//				}
//				if (StringUtils.isNotBlank(userName)) {
//					list.add(cb.equal(root.get("userName").as(String.class), userName));
//				}
//				return cb.and(list.toArray(new Predicate[list.size()]));
//			}
//		};
//		return expenseCalendarRepository.findAll(specification, pageable);
		if(StringUtils.isNotBlank(userName)){
			return  expenseCalendarRepository.findByInviteCodeLikeOrUserNameLikeAndMonthTimeOrderByCreateTimeDesc("%"+inviteCode+"%","%"+ userName+"%",monthTime, pageable);
		}else{
			return  expenseCalendarRepository.findByMonthTimeOrderByCreateTimeDesc(monthTime,pageable);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Page<ExpenseCalendar> findByAllNow(String inviteCode, String userName, Pageable pageable) {
		List<ExpenseCalendar> list = new ArrayList<>();
		List<Wxuser> listWU = null;
		if(StringUtils.isNotBlank(userName)){
			listWU = wxuserRepository.findByInviteCodeLikeOrNickNameLikeAndInviteCodeNotNullOrderByCreateTimeDesc("%"+inviteCode+"%","%"+userName+"%");
		}else{
			listWU = wxuserRepository.findByInviteCodeNotNullOrderByCreateTimeDesc();
		}
		
		
		List<String> listorderopnid = new ArrayList<>();
		List<String> parentInviteCodeList = new ArrayList<>();
		
		
		for(Wxuser wxuser: listWU){
			listorderopnid.add(wxuser.getOpenId());
			parentInviteCodeList.add(wxuser.getInviteCode());
		}
		//最外层批量查询所有用户订单
		List<OrderMasterExpense> listOM = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(
				listorderopnid, OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(0),new Date());

		//最外围批量查询所有一级下游信息
		List<Wxuser> listw = wxuserRepository.findByParentInviteCodeInAndUserGradeNotAndCreateTimeBetween(
				parentInviteCodeList,UserGardeEnum.ZERO.getCode(), DateUtil.getSupportBeginDayofMonth(-1200),new Date());
		List<String> buyerOpenids2 = new ArrayList<>();
		List<String> allparentInviteCodeList2 = new ArrayList<>();
		for(Wxuser wxuser : listw){
			buyerOpenids2.add(wxuser.getOpenId());
			allparentInviteCodeList2.add(wxuser.getInviteCode());
		}
		List<OrderMasterExpense> listOM2 = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(
				buyerOpenids2, OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(0),new Date());//一级下游所有的消费订单
		
		//最外围查询所有二级用户信息
		List<Wxuser> listIndirect = wxuserRepository.findByParentInviteCodeInAndUserGradeNotAndCreateTimeBetween(
				allparentInviteCodeList2,UserGardeEnum.ZERO.getCode(), DateUtil.getSupportBeginDayofMonth(-1200),new Date());
		List<String> buyerOpenids3 = new ArrayList<>();
		for(Wxuser wxuser : listIndirect){
			buyerOpenids3.add(wxuser.getOpenId());
		}
		List<OrderMasterExpense> listOrderE = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(buyerOpenids3,OrderStatusEnum.RECEIVED.getCode(), 
				DateUtil.getSupportBeginDayofMonth(0),new Date());
		
		//
		List<Interests> Interestslist = interestsRepository.findAll();
		
		
		//遍历实时用户
		if (listWU != null && listWU.size()>0) {
			for(Wxuser wu : listWU){
				BigDecimal useramount = BigDecimal.ZERO;
				if (listOM != null && !listOM.isEmpty()) {
					for (OrderMasterExpense om : listOM) {
						if(om.getBuyerOpenid().equals(wu.getOpenId())){
							useramount = useramount.add(om.getOrderCost());
						}
					}
				}
				ExpenseCalendar expenseCalendar = new ExpenseCalendar();
				//组装基本数据
				expenseCalendar.setOpenId(wu.getOpenId());// 用户openID
				expenseCalendar.setUserGrade(wu.getUserGrade());// 权益等级
				expenseCalendar.setExpenseSum(useramount);// 用户自己每月销售金额
				expenseCalendar.setInviteCode(wu.getInviteCode());// 邀请码
				expenseCalendar.setParentInviteCode(wu.getParentInviteCode());// 上级邀请码
				expenseCalendar.setJoinTime(wu.getCreateTime());// 加入时间
				expenseCalendar.setUserName(wu.getNickName());//用户名字
				expenseCalendar.setAvatarUrl(wu.getAvatarUrl());
				
				Interests interests = null;
				for(Interests ii : Interestslist){
					if(ii.getUserGrade().equals(wu.getUserGrade())){
						interests = ii;
					}
				}
				expenseCalendar.setSalesDistribution(interests.getSalesDistribution());
				expenseCalendar.setExpressDiscount(interests.getExpressDiscount());
				expenseCalendar.setProductDiscount(interests.getProductDiscount());
				expenseCalendar.setIndirectSalesDistribution(interests.getIndirectSalesDistribution());
				expenseCalendar.setMonthTime("当前月");
				
				//组装一级下游数据
				
				BigDecimal amountAll = BigDecimal.ZERO;
				int n1 = 0;
				List<String> list2 =new ArrayList<>();
				List<String> parentInviteCodeList2 =new ArrayList<>();//对应二级用户的上级邀请码
				for(Wxuser wxuser : listw){
					if(wu.getInviteCode().toUpperCase().equals(wxuser.getParentInviteCode().toUpperCase())){
						n1 = n1 + 1;
						list2.add(wxuser.getOpenId());
						parentInviteCodeList2.add(wxuser.getInviteCode());
					}
				}
				for(String aa : list2){
					for(OrderMasterExpense orderMasterExpense : listOM2){
						if(aa.equals(orderMasterExpense.getBuyerOpenid())){
							amountAll = amountAll.add(orderMasterExpense.getOrderCost());
						}
					}
				}
				
				expenseCalendar.setDownstreamPeople(n1);// 本月下游人数
				expenseCalendar.setDownstreamSum(amountAll);// 下游用户每月消费金额
				expenseCalendar.setSalesDistributionSum(amountAll.multiply(interests.getSalesDistribution()).setScale(2, BigDecimal.ROUND_HALF_UP));// 下游用户每月返点金额
				
				//组装二级下游数据
				int indirectDownstreamPeople = 0;
				BigDecimal indirectSalesDistributionSum = BigDecimal.ZERO;
				List<String> list3 =new ArrayList<>();
				if (listw != null && listw.size() > 0) {
					for(String bb : parentInviteCodeList2){
						for(Wxuser wxuser : listIndirect){
							if(wxuser.getParentInviteCode().toUpperCase().equals(bb.toUpperCase())){
								indirectDownstreamPeople = indirectDownstreamPeople + 1;
								list3.add(wxuser.getOpenId());
							}
						}
					}
					for(String bb: list3){
						for(OrderMasterExpense orderMasterExpense : listOrderE){
							if(bb.equals(orderMasterExpense.getBuyerOrderId())){
								indirectSalesDistributionSum = indirectSalesDistributionSum.add(orderMasterExpense.getOrderCost());
							}
						}
					}
				}
				expenseCalendar.setIndirectDownstreamPeople(indirectDownstreamPeople);// 间接下游发展人数
				expenseCalendar.setIndirectDownstreamSum(indirectSalesDistributionSum);// 间接下游每月销售总和
				expenseCalendar.setIndirectSalesDistributionSum(indirectSalesDistributionSum.multiply(interests.getIndirectSalesDistribution()).setScale(2, BigDecimal.ROUND_HALF_UP));// 间接下游每月返点金额

				list.add(expenseCalendar);
			}
		}
		
		//排序咯
		Collections.sort(list,new Comparator () {
            @Override
            public int compare(Object o1, Object o2) {
            	ExpenseCalendar e1 = (ExpenseCalendar) o1;
            	ExpenseCalendar e2 = (ExpenseCalendar) o2;
                return e2.getExpenseSum().compareTo(e1.getExpenseSum());
            }
        });
		
		List<ExpenseCalendar> listend = new ArrayList<>();
		if(list.size() >0){
			if(pageable.getPageNumber()*pageable.getPageSize() < list.size()){
				if((pageable.getPageNumber()+1)*pageable.getPageSize() < list.size()){
					listend = list.subList(pageable.getPageNumber()*pageable.getPageSize(), (pageable.getPageNumber()+1)*pageable.getPageSize());
				}else{
					listend = list.subList(pageable.getPageNumber()*pageable.getPageSize(), list.size());
				}
			}
		}
		
		return new PageImpl<>(listend, pageable, listWU.size());
	}
	
	

	@Override
	public List<ExpenseCalendar> findByOpenIdAndMonthTimeLike(String openId, String monthTime) {
		if (openId == null) {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
		if (monthTime == null) {
			return expenseCalendarRepository.findByOpenId(openId);
		} else {
			return expenseCalendarRepository.findByOpenIdAndMonthTimeLike(openId, monthTime);
		}
	}

	@Override
	public ExpenseCalendar findByOpenIdAndMonthTime(String openId, String monthTime) {
		if (openId == null || monthTime == null) {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
		return expenseCalendarRepository.findByOpenIdAndMonthTime(openId, monthTime);
	}

	@Override
	public BigDecimal getNowExpenseSum(String openId) {
		if (openId == null) {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
		// 查询用户当前的权益等级
		Wxuser wu = wxuserRepository.findByOpenId(openId);
		Interests interests = interestsRepository.findByUserGrade(wu.getUserGrade());
		// 本月他的所有的下游用户返点
		List<Wxuser> listw = wxuserRepository.findByParentInviteCodeAndUserGradeNotAndCreateTimeBetween(wu.getInviteCode(),UserGardeEnum.ZERO.getCode(),
				DateUtil.getSupportBeginDayofMonth(-1200), new Date());
		BigDecimal amountAll = BigDecimal.ZERO;
		if (listw != null && listw.size() > 0) {
			List<String> parentInviteCodeList = new ArrayList<String>();//用户上级邀请码,用来查询间接下游用户
			List<String> buyerOpenidList = new ArrayList<String>();//买手openid,用来批量查询下游买手订单
			for (Wxuser wxuser : listw) {
				parentInviteCodeList.add(wxuser.getInviteCode());
				buyerOpenidList.add(wxuser.getOpenId());
			}
			
			// 遍历下游用户的这个月销售金额
			List<OrderMasterExpense> listOM = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(
					buyerOpenidList, OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(0),
					new Date());
			if (listOM != null && !listOM.isEmpty()) {
				for (OrderMasterExpense om : listOM) {
					amountAll = amountAll.add(om.getOrderCost());
				}
			}
			// 遍历间接下游的用户
			List<Wxuser> listw2 = wxuserRepository.findByParentInviteCodeInAndUserGradeNotAndCreateTimeBetween(parentInviteCodeList,UserGardeEnum.ZERO.getCode(), DateUtil.getSupportBeginDayofMonth(-1200), new Date());
			if(listw2 != null && listw2.size() > 0){
				List<String> buyerOpenidList2 = new ArrayList<String>(); 
				for (Wxuser wxuser2 : listw2) {
					buyerOpenidList2.add(wxuser2.getOpenId());
				}
				List<OrderMasterExpense> listOM2 = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(buyerOpenidList2,
						OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(0),new Date());
				if (listOM2 != null && !listOM2.isEmpty()) {
					for (OrderMasterExpense om : listOM2) {
						amountAll = amountAll.add(om.getOrderCost());
					}
				}
			}
		}
		return amountAll.multiply(interests.getSalesDistribution()).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	@Override
	public void updateInteger(Interests interests) {
		if (interests == null || interests.getInterestsId() == null) {
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
		interestsRepository.save(interests);

	}

	/**
	 * 判断用户这个月权益等级
	 * 
	 * @param a
	 *            上个月销售金额
	 * @param b
	 *            上个月用户发展下线人数
	 * @return
	 */
	private int decideUserGarde(BigDecimal a, int b) {
		int aa = 0;
		int bb = 0;
		if (a.compareTo(new BigDecimal(1000)) >= 0) {
			aa = 1;
		}
		if (a.compareTo(new BigDecimal(30000)) >= 0) {
			aa = 2;
		}
		if (a.compareTo(new BigDecimal(80000)) >= 0) {
			aa = 3;
		}
		if (a.compareTo(new BigDecimal(150000)) >= 0) {
			aa = 4;
		}
		if (a.compareTo(new BigDecimal(250000)) >= 0) {
			aa = 5;
		}
		if (a.compareTo(new BigDecimal(400000)) >= 0) {
			aa = 6;
		}
		if (a.compareTo(new BigDecimal(600000)) >= 0) {
			aa = 7;
		}
		if (a.compareTo(new BigDecimal(850000)) >= 0) {
			aa = 8;
		}
		if (a.compareTo(new BigDecimal(1200000)) >= 0) {
			aa = 9;
		}
		if (a.compareTo(new BigDecimal(1600000)) >= 0) {
			aa = 10;
		}
		if (a.compareTo(new BigDecimal(2000000)) >= 0) {
			aa = 11;
		}

		if (b >= 1) {
			bb = 1;
		}
		if (b >= 5) {
			bb = 2;
		}
		if (b >= 10) {
			bb = 3;
		}
		if (b >= 20) {
			bb = 4;
		}
		if (b >= 30) {
			bb = 5;
		}
		if (b >= 50) {
			bb = 6;
		}
		if (b >= 75) {
			bb = 7;
		}
		if (b >= 100) {
			bb = 8;
		}
		if (b >= 150) {
			bb = 9;
		}
		if (b >= 200) {
			bb = 10;
		}
		if (b >= 250) {
			bb = 11;
		}

		if (aa > bb) {
			return aa;
		}
		return bb;
	}

	/**
	 * 得到某个月 。-1上个月，1下个月，0当前月<br>
	 * 格式:yyyy-MM<br>
	 * 
	 * @param a
	 * @return
	 */
	private String getFrontMonth(int a) {
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(new Date());
		endDate.add(Calendar.MONTH, a); // 月份减
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		String dateString = formatter.format(endDate.getTime());
		return dateString;
	}

	@Override
	public void updateOrderStatus() {
		// 查询所有订单状态不为 已收货的订单
		List<Integer> orderStatusList = new ArrayList<Integer>();
		orderStatusList.add(OrderStatusEnum.DELIVERED.getCode());
		List<OrderMaster> list = orderMasterRepository.findByOrderStatusIn(orderStatusList);
		// 查询物流状态
		List<OrderMaster> list2 = new ArrayList<OrderMaster>();
		List<OrderMasterExpense> list3 = new ArrayList<OrderMasterExpense>();
		if (list != null && !list.isEmpty()) {
			for (OrderMaster orderMaster : list) {
				EmsResponse emsResponse = null;
				try {
					JSONObject jSONObject = eMSUtil.query(orderMaster.getExpressNumber());
					emsResponse = JSONObject.toJavaObject(jSONObject, EmsResponse.class);
				} catch (Exception e) {
					log.error("查询物流单号异常", e.getMessage());
					continue;// 结束本次操作
				}
				if (emsResponse != null && emsResponse.getTraces() != null && !emsResponse.getTraces().isEmpty()) {
					for (EmsBean emsBean : emsResponse.getTraces()) {
						if (emsBean.getRemark().indexOf("投递并签收") >= 0) {
							orderMaster.setOrderStatus(OrderStatusEnum.RECEIVED.getCode());
							list2.add(orderMaster);
							OrderMasterExpense orderMasterExpense = new OrderMasterExpense();
							BeanUtils.copyProperties(orderMaster, orderMasterExpense);
							list3.add(orderMasterExpense);
						}
					}
				}
			}
		}

		// 修改订单状态
		orderMasterRepository.save(list2);
		//复制信息到别表
		orderMasterExpenseRepository.save(list3);
		
	}

	@Override
	public void cancelOrder() {
		// 查询状态为下单未支付的订单
		List<OrderMaster> list = orderMasterRepository.findByOrderStatus(OrderStatusEnum.NEW.getCode());
		long timeNow = (new Date()).getTime();
		for (OrderMaster ord : list) {
			if ((timeNow - ord.getCreateTime().getTime()) > 1000 * 60 * 5) {
				OrderDto orderDto = new OrderDto();
				BeanUtils.copyProperties(ord, orderDto);
				List<OrderDetail> listOrderDetail = orderDetailRepository.findByOrderId(ord.getOrderId());
				orderDto.setOrderDetailList(listOrderDetail);
				try {
					cancelOrderDetail(orderDto);
				} catch (Exception e) {
					System.out.println("========" + e.getMessage());
					log.error("取消5分钟没付款的订单出错==" + e.getMessage());
				}
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	private void cancelOrderDetail(OrderDto orderDto) {
		// 2. 修改订单状态
		OrderMaster orderMaster = new OrderMaster();
		BeanUtils.copyProperties(orderDto, orderMaster);
		orderMaster.setOrderStatus(OrderStatusEnum.CANCELED.getCode());
		OrderMaster updateResult = orderMasterRepository.save(orderMaster);
		if (updateResult == null) {
			log.error("【取消订单】更新失败, orderMaster = {}", orderMaster);
			throw new GlobalException(ResultEnum.ORDER_UPDATE_FAIL);
		}
		// 3. 返还库存
		if (CollectionUtils.isEmpty(orderDto.getOrderDetailList())) {
			log.error("【取消订单】订单中无商品详情, orderDto = {}", orderDto);
			throw new GlobalException(ResultEnum.ORDER_DETAIL_EMPTY);
		}
		shopService.increaseStock(orderDto.getOrderDetailList());
	}

	@Override
	public ResponseEntity<byte[]> exportExcel(String time) {

		List<ExpenseCalendar> list = new ArrayList<>();
		if (StringUtils.isNotBlank(time)) {
			list = expenseCalendarRepository.findByMonthTime(time);
		} else {
			list = expenseCalendarRepository.findByMonthTime(getFrontMonth(-1));
		}

		HttpHeaders headers = null;
		ByteArrayOutputStream baos = null;

		try {
			// 1. 创建 excel 文档
			HSSFWorkbook workbook = new HSSFWorkbook();

			// 2. 创建文档摘要
			workbook.createInformationProperties();
			// 3. 设置文档的基本信息
			DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

			dsi.setCategory("集栈供应链用户返点详情列表");
			dsi.setManager("陈谦");
			dsi.setCompany("杭州集栈网络科技有限公司");

			SummaryInformation si = workbook.getSummaryInformation();
			si.setSubject("返点详情列表");
			si.setTitle("返点详情列表");
			si.setAuthor("杭州集栈网络科技有限公司");
			si.setComments("技术支持：杭州集栈网络科技有限公司");

			HSSFSheet sheet = workbook.createSheet("集栈供应链返点详情列表");

			// 4. 创建日期显示格式 (备用)
			HSSFCellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
			// 5. 创建标题的显示样式
			HSSFCellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

//			sheet.setColumnWidth(0, 60 * 256);
//			sheet.setColumnWidth(1, 12 * 256);
//			sheet.setColumnWidth(2, 35 * 256);
//			sheet.setColumnWidth(3, 16 * 256);
//			sheet.setColumnWidth(4, 10 * 256);
//			sheet.setColumnWidth(5, 20 * 256);
//			sheet.setColumnWidth(6, 12 * 256);
//
//			sheet.setColumnWidth(7, 8 * 256);
//			sheet.setColumnWidth(8, 15 * 256);
//			sheet.setColumnWidth(9, 15 * 256);
//			sheet.setColumnWidth(10, 15 * 256);
//			sheet.setColumnWidth(11, 10 * 256);
//			sheet.setColumnWidth(12, 15 * 256);
//			sheet.setColumnWidth(13, 10 * 256);
//			sheet.setColumnWidth(14, 10 * 256);
//			sheet.setColumnWidth(15, 10 * 256);
//			sheet.setColumnWidth(16, 15 * 256);
//			sheet.setColumnWidth(17, 15 * 256);
			

			HSSFRow headerRow = sheet.createRow(0);

			HSSFCell headCell0 = headerRow.createCell(0);
			headCell0.setCellValue("微信用户的openid");
			headCell0.setCellStyle(headerStyle);

			HSSFCell headCell1 = headerRow.createCell(1);
			headCell1.setCellValue("用户名字");
			headCell1.setCellStyle(headerStyle);

			HSSFCell headCell2 = headerRow.createCell(2);
			headCell2.setCellValue("用户等级");
			headCell2.setCellStyle(headerStyle);

			HSSFCell headCell3 = headerRow.createCell(3);
			headCell3.setCellValue("加入集栈日期");
			headCell3.setCellStyle(headerStyle);

			HSSFCell headCell4 = headerRow.createCell(4);
			headCell4.setCellValue("用户自己每月的消费金额");
			headCell4.setCellStyle(headerStyle);

			HSSFCell headCell5 = headerRow.createCell(5);
			headCell5.setCellValue("月份");
			headCell5.setCellStyle(headerStyle);

			HSSFCell headCell6 = headerRow.createCell(6);
			headCell6.setCellValue("物流折扣");
			headCell6.setCellStyle(headerStyle);

			HSSFCell headCell7 = headerRow.createCell(7);
			headCell7.setCellValue("商品折扣");
			headCell7.setCellStyle(headerStyle);

			HSSFCell headCell8 = headerRow.createCell(8);
			headCell8.setCellValue("下游分销返点比例");
			headCell8.setCellStyle(headerStyle);

			HSSFCell headCell9 = headerRow.createCell(9);
			headCell9.setCellValue("间接下游返点比例");
			headCell9.setCellStyle(headerStyle);

			HSSFCell headCell10 = headerRow.createCell(10);
			headCell10.setCellValue("用户邀请码");
			headCell10.setCellStyle(headerStyle);

			HSSFCell headCell11 = headerRow.createCell(11);
			headCell11.setCellValue("上级的邀请码");
			headCell11.setCellStyle(headerStyle);

			HSSFCell headCell12 = headerRow.createCell(12);
			headCell12.setCellValue("本月新增下游发展人数");
			headCell12.setCellStyle(headerStyle);

			HSSFCell headCell13 = headerRow.createCell(13);
			headCell13.setCellValue("下游销售额总和");
			headCell13.setCellStyle(headerStyle);

			HSSFCell headCell14 = headerRow.createCell(14);
			headCell14.setCellValue("下游每月返点金额");
			headCell14.setCellStyle(headerStyle);

			HSSFCell headCell15 = headerRow.createCell(15);
			headCell15.setCellValue("本月新增间接下游人数");
			headCell15.setCellStyle(headerStyle);

			HSSFCell headCell16 = headerRow.createCell(16);
			headCell16.setCellValue("本月间接下游销售额总和");
			headCell16.setCellStyle(headerStyle);

			HSSFCell headCell17 = headerRow.createCell(17);
			headCell17.setCellValue("下游间接每月返点金额");
			headCell17.setCellStyle(headerStyle);

			if(list != null && list.size() >0){
				for (int i = 0; i < list.size(); i++) {
					HSSFRow row = sheet.createRow(i + 1);
					ExpenseCalendar expenseCalendar = list.get(i);
	
					row.createCell(0).setCellValue(expenseCalendar.getOpenId());
					row.createCell(1).setCellValue(expenseCalendar.getUserName());
					row.createCell(2).setCellValue(expenseCalendar.getUserGrade().toString());
					row.createCell(3).setCellValue(DateUtil.dateToString(expenseCalendar.getJoinTime()));
					row.createCell(4).setCellValue(expenseCalendar.getExpenseSum().toString());
					row.createCell(5).setCellValue(expenseCalendar.getMonthTime());
					row.createCell(6).setCellValue(expenseCalendar.getExpressDiscount().toString());
					row.createCell(7).setCellValue(expenseCalendar.getProductDiscount().toString());
					row.createCell(8).setCellValue(expenseCalendar.getSalesDistribution().toString());
					row.createCell(9).setCellValue(expenseCalendar.getIndirectSalesDistribution().toString());
					row.createCell(10).setCellValue(expenseCalendar.getInviteCode());
					row.createCell(11).setCellValue(expenseCalendar.getParentInviteCode());
					row.createCell(12).setCellValue(expenseCalendar.getDownstreamPeople().toString());
					row.createCell(13).setCellValue(expenseCalendar.getDownstreamSum().toString());
					row.createCell(14).setCellValue(expenseCalendar.getSalesDistributionSum().toString());
					row.createCell(15).setCellValue(expenseCalendar.getIndirectDownstreamPeople().toString());
					row.createCell(16).setCellValue(expenseCalendar.getIndirectDownstreamSum().toString());
					row.createCell(17).setCellValue(expenseCalendar.getIndirectSalesDistributionSum().toString());
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
			String dateStr = sdf.format(new Date());

			String fileName = "用户返点导出" + dateStr + ".xls";

			headers = new HttpHeaders();
			headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("utf-8"), "iso-8859-1"));
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			baos = new ByteArrayOutputStream();
			workbook.write(baos);

		} catch (Exception e) {
			log.error("【Excel导出】导出异常：{}", e);
			throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
		}
		return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.CREATED);
	}

	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> detailsByOpenid(String openid,PageRequest pageRequest) {
		Map<String, Object> map = new HashMap<String,Object>();
		List<ExpenseCalendarDto> list = new ArrayList<ExpenseCalendarDto>();
		// 1.查询用户信息
		Wxuser wu = wxuserRepository.findByOpenId(openid);
		if(wu == null){
			throw new GlobalException(ResultEnum.USER_NOT_EXIST);
		}
		
		Interests interests = interestsRepository.findByUserGrade(wu.getUserGrade());//分销比例
		int wpn = 0;//间接下游人员数量
		BigDecimal wallAmot = BigDecimal.ZERO;//间接下游用户总金额
		
		//下游等级不为0的用户
		List<Wxuser> listwpage = wxuserRepository.findByParentInviteCodeAndUserGradeNotAndCreateTimeBetween(wu.getInviteCode(),UserGardeEnum.ZERO.getCode(),
				DateUtil.getSupportBeginDayofMonth(-1200), new Date());
		if (listwpage != null && listwpage.size()>0) {
			
			List<String> buyerOpenids = new ArrayList<>();
			for (Wxuser wxuser : listwpage) {
				buyerOpenids.add(wxuser.getOpenId());
			}
			
			List<OrderMasterExpense> listOM = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(
					buyerOpenids, OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(0),
					new Date());
			if (listOM != null && !listOM.isEmpty()) {
				for(Wxuser wxuser : listwpage){
					BigDecimal amountAll = BigDecimal.ZERO;//下游用户金额
					for (OrderMasterExpense om : listOM) {
						if(wxuser.getOpenId().equals(om.getBuyerOpenid())){
							amountAll = amountAll.add(om.getOrderCost());
						}
					}
					
					ExpenseCalendarDto expenseCalendarDto = new ExpenseCalendarDto();
					expenseCalendarDto.setUserName(wxuser.getNickName());
					expenseCalendarDto.setUrl(wxuser.getAvatarUrl());
					expenseCalendarDto.setUserGrade(wxuser.getUserGrade());
					expenseCalendarDto.setInviteCode(wxuser.getInviteCode());
					expenseCalendarDto.setExpenseSum(amountAll);
					expenseCalendarDto.setSum(amountAll.multiply(interests.getSalesDistribution()).setScale(2, BigDecimal.ROUND_HALF_UP));
					list.add(expenseCalendarDto);
				}
			}
		}
		//下游用户等级为0的用户
		List<Wxuser> listuser0 = wxuserRepository.findByParentInviteCodeAndUserGradeAndCreateTimeBetween(wu.getInviteCode(),UserGardeEnum.ZERO.getCode(),
				DateUtil.getSupportBeginDayofMonth(-1200), new Date());
		if (listuser0 != null && listuser0.size()>0) {
			for(Wxuser wxuser : listuser0){
				ExpenseCalendarDto expenseCalendarDto = new ExpenseCalendarDto();
				expenseCalendarDto.setUserName(wxuser.getNickName());
				expenseCalendarDto.setUrl(wxuser.getAvatarUrl());
				expenseCalendarDto.setUserGrade(wxuser.getUserGrade());
				expenseCalendarDto.setInviteCode(wxuser.getInviteCode());
				expenseCalendarDto.setExpenseSum(BigDecimal.ZERO);
				expenseCalendarDto.setSum(BigDecimal.ZERO);
				list.add(expenseCalendarDto);
			}
		}
		
		
		
		//根据消费金额排序
		Collections.sort(list,new Comparator () {
            @Override
            public int compare(Object o1, Object o2) {
            	ExpenseCalendarDto e1 = (ExpenseCalendarDto) o1;
            	ExpenseCalendarDto e2 = (ExpenseCalendarDto) o2;
                return e2.getSum().compareTo(e1.getSum());
            }
        });
		int totalPages = 0;
		if((list.size()%pageRequest.getPageSize()) > 0){
			totalPages = list.size()/pageRequest.getPageSize()+1;
		}else{
		   totalPages = list.size()/pageRequest.getPageSize();
		}
		List<ExpenseCalendarDto> listend = new ArrayList<>();
		if(list.size()>0){
			if(pageRequest.getPageNumber()*pageRequest.getPageSize() < list.size()){
				if((pageRequest.getPageNumber()+1)*pageRequest.getPageSize() < list.size()){
					listend = list.subList(pageRequest.getPageNumber()*pageRequest.getPageSize(), (pageRequest.getPageNumber()+1)*pageRequest.getPageSize());
				}else{
					listend = list.subList(pageRequest.getPageNumber()*pageRequest.getPageSize(), list.size());
				}
			}
		}
		
		// 2.计算用户下游返点信息
		List<Wxuser> listw = wxuserRepository.findByParentInviteCodeAndUserGradeNotAndCreateTimeBetween(wu.getInviteCode(),UserGardeEnum.ZERO.getCode(),
				DateUtil.getSupportBeginDayofMonth(-1200), new Date());
		if (listw != null && listw.size() > 0) {
			List<String> buyerOpenidList = new ArrayList<String>();
			for (Wxuser wxuser : listw) {
				buyerOpenidList.add(wxuser.getOpenId());
			}
			// 3.遍历间接下游的用户
			List<Wxuser> listw2 = wxuserRepository.findByParentInviteCodeInAndUserGradeNotAndCreateTimeBetween(buyerOpenidList, UserGardeEnum.ZERO.getCode(),DateUtil.getSupportBeginDayofMonth(-1200), new Date());
			if(listw2 != null && listw2.size()>0){
				wpn =  listw2.size();
				List<String> listv = new ArrayList<String>();
				for (Wxuser wxuser2 : listw2) {
					listv.add(wxuser2.getOpenId());
				}	
				List<OrderMasterExpense> listOM2 = orderMasterExpenseRepository.findByBuyerOpenidInAndOrderStatusAndUpdateTimeBetween(listv,
						OrderStatusEnum.RECEIVED.getCode(), DateUtil.getSupportBeginDayofMonth(0),new Date());
				if (listOM2 != null && !listOM2.isEmpty()) {
					for (OrderMasterExpense om : listOM2) {
						wallAmot = wallAmot.add(om.getOrderCost());
					}
				}
			}
		}
		map.put("list", listend);	
		map.put("wpn", wpn);
		map.put("wallAmot", wallAmot);
		map.put("totalPages", totalPages);
		map.put("totalNum", list.size());
		return map;
	}

	
	@Override
	public Map<String, Object> detailsByExpenseCalendarId(Integer expenseCalendarId,PageRequest pageRequest){
		 Map<String, Object> map = new HashMap<String, Object>();
		 List<ExpenseCalendarDto> list = new ArrayList<ExpenseCalendarDto>();
		ExpenseCalendar expenseCalendar = expenseCalendarRepository.findOne(expenseCalendarId);
		if(expenseCalendar == null){
			throw new GlobalException(ResultEnum.USER_NOT_EXIST);
		}
		//查询下游用户
		Page<ExpenseCalendar> page =  expenseCalendarRepository.findByParentInviteCode(expenseCalendar.getInviteCode(), pageRequest);
		
		if(page != null && page.getNumberOfElements() > 0){
			for(ExpenseCalendar ex : page.getContent()){
				ExpenseCalendarDto expenseCalendarDto = new ExpenseCalendarDto();
				expenseCalendarDto.setInviteCode(ex.getInviteCode());
				expenseCalendarDto.setExpenseSum(ex.getExpenseSum());
				expenseCalendarDto.setUserGrade(ex.getUserGrade());
				expenseCalendarDto.setUserName(ex.getUserName());
				expenseCalendarDto.setUrl(ex.getAvatarUrl());
				list.add(expenseCalendarDto);
			}
		}
		//排序
		
		
		map.put("expenseCalendarPage", page.getTotalElements());
		map.put("list", list);	
		map.put("totalPages", page.getTotalPages());
		return map;
	}

	@Override
	public void updateExpensewWhiteStatus(String openid,Integer status) {
		
		Wxuser wxuser = wxuserRepository.findByOpenId(openid);
		if(wxuser == null || status == null){
			throw new GlobalException(ResultEnum.USER_NOT_EXIST);
		}
		if(!status.equals(ExpensewWhiteStatusEnum.CLOSE.getCode()) && !status.equals(ExpensewWhiteStatusEnum.OPEN.getCode())){
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
		wxuser.setExpenseWhiteStatus(status);
		wxuserRepository.save(wxuser);
		
	}

	@Override
	public List<Wxuser> userList() {
		List<Wxuser> list = new ArrayList<>();
		List<Wxuser> listWxuser = wxuserRepository.findByInviteCodeNotNull();
		
		if(!CollectionUtils.isEmpty(listWxuser)){
			for(Wxuser wxuser : listWxuser){
				Wxuser wu = new Wxuser();
				wu.setNickName(wxuser.getNickName());
				wu.setUserGrade(wxuser.getUserGrade());
				wu.setAvatarUrl(wxuser.getAvatarUrl());
				list.add(wu);
			}
		}
		
		return list;
	}
	

	
	
	

}
