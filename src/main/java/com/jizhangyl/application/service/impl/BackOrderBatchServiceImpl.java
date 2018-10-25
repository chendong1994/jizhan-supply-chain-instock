package com.jizhangyl.application.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.jizhangyl.application.VO.ShopExportVo;
import com.jizhangyl.application.config.WechatAccountConfig;
import com.jizhangyl.application.converter.OrderMaster2OrderDtoConverter;
import com.jizhangyl.application.converter.OrderMaster2OrderExportConverter;
import com.jizhangyl.application.dataobject.primary.ExpressNumYto;
import com.jizhangyl.application.dataobject.primary.OrderBatch;
import com.jizhangyl.application.dataobject.primary.OrderBatchRelation;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderExport;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.primary.Shop;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.OrderBatchRelationRepository;
import com.jizhangyl.application.repository.primary.OrderBatchRepository;
import com.jizhangyl.application.repository.primary.OrderMasterRepository;
import com.jizhangyl.application.repository.primary.WxuserAddrRepository;
import com.jizhangyl.application.repository.secondary.WxuserRepository;
import com.jizhangyl.application.service.AddressResolveService;
import com.jizhangyl.application.service.BackOrderBatchService;
import com.jizhangyl.application.service.ExpressNumYtoService;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.service.WxuserAddrService;
import com.jizhangyl.application.utils.IpUtil;
import com.jizhangyl.application.utils.JsonUtil;
import com.jizhangyl.application.utils.KeyUtil;
import com.jizhangyl.application.utils.MathUtil;
import com.jizhangyl.application.utils.PayUtil;
import com.jizhangyl.application.utils.PriceUtil;
import com.jizhangyl.application.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BackOrderBatchServiceImpl implements BackOrderBatchService{
	
	private static final String BODY = "微信扫码支付";
	// 35 元 1 公斤
    private static final double EXPRESS_UNIT = 35;
	
	@Autowired
    private WxuserAddrRepository wxuserAddrRepository;
	@Autowired
    private OrderMasterRepository orderMasterRepository;
    @Autowired
    private ExpressNumYtoService expressNumYtoService;
    @Autowired
    private OrderMasterService orderMasterService;
	@Autowired
	private AddressResolveService addressResolveService;
	@Autowired
    private WxPayService wxPayService;
	@Autowired
    private  OrderBatchRepository orderBatchRepository;
	@Autowired
    private OrderBatchRelationRepository orderBatchRelationRepository;
	@Autowired
    private WechatAccountConfig accountConfig;
	@Autowired
    private SmsUtil smsUtil;
	@Autowired
    private OrderMasterService orderService;
	@Autowired
    private ShopService shopService;
	@Autowired
	private WxuserAddrService wxuserAddrService;
	@Autowired
    private OrderMaster2OrderExportConverter orderMaster2OrderExportConverter;
	@Autowired
    private WxuserRepository wxuserRepository;
	 
	
	private static final String url = "http://www.jizhangyl.com/jizhangyl/jizhangyl/back/pay/notify";
			

	@Transactional(rollbackFor = Exception.class)
	@Override
	public synchronized Map<String,Object> addBatchOrder(List<OrderDto> list, HttpServletRequest request) {
		Map<String,Object> map = new HashMap<String,Object>();
		BigDecimal orderAmountAll = BigDecimal.ZERO;//批量导入成功订单的总金额
		List<OrderBatchRelation> listOrderBaych = new ArrayList<>();//批量订单关系表
		String id = KeyUtil.genKey()+KeyUtil.genUniqueKey();//生成主键
		
		//  判断用户订单金额,商品数量是否超标
		BigDecimal amo = upperLimit(list);
		if(amo.compareTo(new BigDecimal(10000))>0){
			return map;
		}
		
		orderAmountAll = batchAddOrder(list, orderAmountAll, listOrderBaych, id);
		OrderBatch orderBatch = new OrderBatch();
		orderBatch.setOrderBatchId(id); 
		orderBatch.setOrderAmountAll(orderAmountAll);
		orderBatch.setOpenId(list.get(0).getBuyerOpenid());
		orderBatchRepository.save(orderBatch);
		orderBatchRelationRepository.save(listOrderBaych);
		
		//2.================调用微信预支付下单接口，批量下单，生成一个组装的订单号，生成付钱二维码，返回前段
		
		// 组装微信二维码生成参数
		 try {
	            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
	            orderRequest.setAppid(accountConfig.getMpAppId());//公众账号ID	
	            orderRequest.setMchId(accountConfig.getMchId());//商户号
	            String nonce_str = PayUtil.getNonceStr();// 设置随机字符串
	            orderRequest.setNonceStr(nonce_str);
	            orderRequest.setBody(BODY);//商品描述
	            orderRequest.setOutTradeNo(id);// 批量主键订单号
	            // 金额为整数，单位为分
	            // TODO 用于测试，改为 1
	            orderRequest.setTotalFee(PriceUtil.yuanToFee(orderAmountAll));
//	            orderRequest.setTotalFee(1);
	            orderRequest.setSpbillCreateIp(IpUtil.getIp(request)); // 用户IP地址
	            orderRequest.setTradeType(WxPayConstants.TradeType.NATIVE); // tradeType -> 支付方式
	            orderRequest.setNotifyUrl(url);//回调url
	            orderRequest.setProductId(id);
	            
	            String params = "appid="+accountConfig.getMpAppId()+"&body="+BODY+"&mch_id="+accountConfig.getMchId()+"&nonce_str="+nonce_str+
	            		"&notify_url="+url+"&out_trade_no="+id+"&product_id="+id +"&spbill_create_ip="+IpUtil.getIp(request)+"&total_fee="+
	            		PriceUtil.yuanToFee(orderAmountAll)+
//                          1+	            		
	            		"&trade_type="+WxPayConstants.TradeType.NATIVE+"&key="+accountConfig.getMchKey();
	            		
	            String paySign = DigestUtils.md5Hex(params).toUpperCase();
	            orderRequest.setSign(paySign);
	            
	            // 2. 调用统一下单接口
	            WxPayUnifiedOrderResult orderResult = wxPayService.unifiedOrder(orderRequest);
	            map.put("url", orderResult.getCodeURL());//返回微信二维码链接
	        } catch (Exception e) {
	            log.error("【微信支付】支付失败，订单号 = {}, 原因 = {}", id, e.getMessage());
	            e.printStackTrace();
	        }
		
		return map;
	}


	@Transactional(rollbackFor = Exception.class)
	private BigDecimal batchAddOrder(List<OrderDto> list, BigDecimal orderAmountAll,
			List<OrderBatchRelation> listOrderBaych, String id) {
		for(OrderDto orderDto : list){
			try {
				OrderDto resultOrderDto = orderMasterService.create(orderDto, true);
				orderAmountAll = orderAmountAll.add(resultOrderDto.getOrderAmount());//订单金额
				OrderBatchRelation orderBatchRelation = new OrderBatchRelation();
				orderBatchRelation.setOrderBatchId(id);
				orderBatchRelation.setOrderId(resultOrderDto.getOrderId());
				listOrderBaych.add(orderBatchRelation);
			} catch (Exception e) {
				throw new RuntimeException(orderDto.getRecipient()+"的订单"+e.getMessage());
			}
		}
		return orderAmountAll;
	}



	@Override
	public List<WxuserAddr> getAddrs(String addrs) {
		JSONObject  jSONObject = addressResolveService.resolveBatch(addrs.toString());
		List<Map<String, String>> list = (List<Map<String, String>>) jSONObject.get("data");
		List<WxuserAddr> userAddrList = new ArrayList<>();
		if(!CollectionUtils.isEmpty(list)){
			for(Map<String, String> map : list){
				WxuserAddr wxuserAddr = new WxuserAddr();
				String provinceName = map.get("province_name");
				String cityName = map.get("city_name");
				String countyName = map.get("county_name");
				String detail = map.get("detail");
				String name = map.get("name");
				String mobile = map.get("mobile");
				wxuserAddr.setReceiver(name);//收件人姓名
				wxuserAddr.setPhone(mobile);//收件人手机号
				wxuserAddr.setProvince(provinceName);//省
				wxuserAddr.setCity(cityName);//市
				wxuserAddr.setArea(countyName);//区
				wxuserAddr.setDetailAddr(detail);//详细地址
				userAddrList.add(wxuserAddr);
			}
		}
		userAddrList = wxuserAddrRepository.save(userAddrList);
		
		return userAddrList;
	}



	@Override
	public WxPayOrderNotifyResult notify(String notifyData) {
		
		// 1. 验证签名
        // 2. 支付的状态
        // 3. 支付金额
        // 4. 支付人（下单人 == 支付人）
        WxPayOrderNotifyResult notifyResult = null;
        try {
            notifyResult = wxPayService.parseOrderNotifyResult(notifyData);
            log.info("【微信支付】异步通知，notifyResult = {}", JsonUtil.toJson(notifyResult));
            // 查询订单
            OrderBatch orderBatch = orderBatchRepository.findOne(notifyResult.getOutTradeNo());

            // 判断订单是否存在
            if (orderBatch == null) {
                log.error("【微信支付】异步通知，订单不存在，orderId = {}", notifyResult.getOutTradeNo());
                throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
            }

            // 判断金额是否一致
            Integer totalFee = PriceUtil.yuanToFee(orderBatch.getOrderAmountAll());
            // TODO, 改为 1 用于测试
            if (!MathUtil.equals(notifyResult.getTotalFee(), totalFee)) {
//            if (!MathUtil.equals(notifyResult.getTotalFee(), 1)) {
                log.error("【微信支付】异步通知，订单金额不一致，orderId = {}, 微信通知金额 = {}, 系统金额 = {}",notifyResult.getOutTradeNo(), notifyResult.getTotalFee(), totalFee);
                throw new GlobalException(ResultEnum.WXPAY_NOTIFY_MONEY_VERIFY_ERROR);
            }

            // 修改订单状态
            List<OrderBatchRelation> listOrderBatchRelation = orderBatchRelationRepository.findByOrderBatchId(orderBatch.getOrderBatchId());
            List<String> ids = new ArrayList<>();
            for(OrderBatchRelation v : listOrderBatchRelation){
            	ids.add(v.getOrderId());
            }
            List<OrderMaster> listOrderMaster = orderMasterRepository.findAll(ids);
            List<OrderDto> listOrderDto = OrderMaster2OrderDtoConverter.convert(listOrderMaster);

            for(OrderDto orderDto : listOrderDto){
            	try {
            		ExpressNumYto unusedNum = updateOrderForBack(orderDto);
            		// 触发短信通知买家查询物流
            		SendSmsResponse response = smsUtil.expressNotify(orderDto.getRecipientPhone(), unusedNum.getExpNum(), null);
            		if (response.getCode() != null && response.getCode().equals("OK")) {
            			log.warn("【微信支付】提醒短信下发成功");
            		} else {
            			log.warn("【微信支付】提醒短信下发失败");
            		}
            	} catch (Exception e) {
            		log.error("【物流单号】单号下发用户失败, {}", e);
            	}
            }

        } catch (WxPayException e) {
            log.error("【微信支付】异步通知异常：{}", e.getMessage());
            e.printStackTrace();
        }
        return notifyResult;
		
	}


	@Transactional(rollbackFor = Exception.class)
	private ExpressNumYto updateOrderForBack(OrderDto orderDto) {
		orderService.paid(orderDto);
		
		// 生成物流单号
		ExpressNumYto unusedNum = expressNumYtoService.findUnused();
		orderDto.setExpressNumber(unusedNum.getExpNum());
		
		// 保存单号至订单主表
		OrderMaster orderMaster = new OrderMaster();
		BeanUtils.copyProperties(orderDto, orderMaster);
		orderService.save(orderMaster);
		
		// 单号标记为已经使用
		expressNumYtoService.updateStatus(unusedNum.getId(), ExpressNumStatusEnum.USED.getCode());
		return unusedNum;
	}
	
	
	
	
	
	/**
	 * 计算批量订单总金额
	 * @param list
	 * @return
	 */
	private BigDecimal upperLimit(List<OrderDto> list){
		// 订单总价
		BigDecimal orderAmount = BigDecimal.ZERO;
		 if(list != null && list.size()>0){
			 
			 Map<String,Integer> map = new HashMap<String,Integer>();//商品id，数量
			 
			 for(OrderDto orderDto : list){
				 // 报关总税金
				 BigDecimal orderTaxes = BigDecimal.ZERO;
				 // 总货值
				 BigDecimal orderCost = BigDecimal.ZERO;
				 BigDecimal orderPackWeight = BigDecimal.ZERO;
				 
				 // 订单详情入库
				 List<OrderDetail> orderCartList = orderDto.getOrderDetailList();
				 List<Integer> productIdList = orderCartList.stream().map(
						 e -> Integer.parseInt(e.getProductId()))
						 .collect(Collectors.toList());
				 
				 for(OrderDetail orderDetail : orderCartList){
					 Integer as = map.get(orderDetail.getProductId());
					 if(as == null){
						 map.put(orderDetail.getProductId(), orderDetail.getProductQuantity()); 
					 }else{
						 map.put(orderDetail.getProductId(), as+orderDetail.getProductQuantity());
					 }
				 }
				 
				 
				 List<Shop> shopList = shopService.findByIdIn(productIdList);
				 if (!CollectionUtils.isEmpty(orderCartList)) {
					 for (OrderDetail od : orderCartList) {
						 Shop shop = null;
						 for (Shop s : shopList) {
							 if (String.valueOf(s.getId()).equals(od.getProductId())) {
								 shop = s;
								 break;
							 }
						 }
						 // 商品供货价
						 BigDecimal productPrice = shop.getShopGprice();
						 // 供货价
						 BigDecimal gPrice = shop.getShopGprice();
						 
						 double packWeight = ((double) (shop.getShopDweight())) / 1000;
						 
						 Integer productQuantity = od.getProductQuantity();
						 // 判断库存是否足够
						 Integer stock = shop.getShopCount() - productQuantity;
						 if (stock < 0) {
							 log.error("【订单入库】商品库存不足, Jancode = {}", shop.getShopJan());
							 throw new GlobalException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH.getCode(), "商品库存不足, Jancode = " + shop.getShopJan());
						 }
						 
						 // 类型的数量转换为 BigDecimal 类型
						 BigDecimal bdProductQuantity = new BigDecimal(productQuantity);
						 
						 // 计算总货值
						 orderCost = productPrice.multiply(bdProductQuantity).add(orderCost);

						 // 计算总打包重量
						 orderPackWeight = new BigDecimal(packWeight).multiply(bdProductQuantity).add(orderPackWeight);
					 }
				 }
				 
				 double calcWeight = computeWeightRound(orderPackWeight.doubleValue());
				 BigDecimal calcFreight = new BigDecimal(calcWeight).multiply(new BigDecimal(EXPRESS_UNIT));
				 
				 // 设置总金额
				 orderAmount = orderAmount.add(orderCost.add(calcFreight).add(orderTaxes));
			 }
			 
			 List<Integer> productIdList2 = new ArrayList<>();
			 for(Map.Entry<String,Integer> mm : map.entrySet()){
				 productIdList2.add(Integer.valueOf(mm.getKey())); 
			 }
			 List<Shop> shopList = shopService.findByIdIn(productIdList2);
			 for(Shop shop : shopList){
				 Integer i = map.get(shop.getId().toString());
				 if(shop.getShopCount() < i){
					 throw new RuntimeException(shop.getShopName()+"的库存不足");  
				 }
			 }
			 
		 }
		 else{
			 throw new RuntimeException("参数为空"); 
		 }
		return orderAmount;
	}
	
	
	
	private double computeWeightRound(double weight) {
        if (weight - 1.0 < 0.0001) {
            return 1;
        }
        double result = weight - (int) weight;
        if (result == 0) {
            return weight;
        } else if (result > 0.5) {
            return (int) weight + 1;
        } else {
            return (int) weight + 0.5;
        }
    }


	@Override
	public ResponseEntity<byte[]> exportBatchOrderExcel(String orderBatchId) {
		
		//查询订单list
		List<OrderBatchRelation> orderBatchRelationList = orderBatchRelationRepository.findByOrderBatchId(orderBatchId);
		
		List<String> orderMasterIdList = orderBatchRelationList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
		
		List<OrderMaster> orderMasterList = orderService.findByOrderIdIn(orderMasterIdList);

        List<Integer> recvAddrIdList = orderMasterList.stream().map(e -> e.getRecipientAddrId()).collect(Collectors.toList());

        List<WxuserAddr> wxuserAddrList = wxuserAddrService.findByIdIn(recvAddrIdList);

        List<OrderExport> orderExportList = orderMaster2OrderExportConverter.convert(orderMasterList, wxuserAddrList);
        
        if(CollectionUtils.isEmpty(orderExportList)){
        	return null;
        }
		
        OrderBatch orderBatch = orderBatchRepository.findOne(orderBatchId);
        Wxuser wxuser = wxuserRepository.findByOpenId(orderBatch.getOpenId());
        String originFileName = wxuser.getNickName();
		HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;

        try {
            // 1. 创建 excel 文档
            HSSFWorkbook workbook = new HSSFWorkbook();

            // 2. 创建文档摘要
            workbook.createInformationProperties();
            // 3. 设置文档的基本信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

            dsi.setCategory("集栈供应链订单列表");
            dsi.setManager("陈谦");
            dsi.setCompany("杭州集栈网络科技有限公司");

            SummaryInformation si = workbook.getSummaryInformation();
            si.setSubject("订单列表");
            si.setTitle("订单列表");
            si.setAuthor("杭州集栈网络科技有限公司");
            si.setComments("技术支持：杭州集栈网络科技有限公司");

            HSSFSheet sheet = workbook.createSheet("集栈供应链订单列表");

            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            sheet.setColumnWidth(0, 60 * 256);
            sheet.setColumnWidth(1, 12 * 256);
            sheet.setColumnWidth(2, 35 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 10 * 256);
            sheet.setColumnWidth(5, 20 * 256);
            sheet.setColumnWidth(6, 12 * 256);

            sheet.setColumnWidth(7, 8 * 256);
            sheet.setColumnWidth(8, 15 * 256);
            sheet.setColumnWidth(9, 15 * 256);
            sheet.setColumnWidth(10, 15 * 256);
            sheet.setColumnWidth(11, 10 * 256);
            sheet.setColumnWidth(12, 15 * 256);
            sheet.setColumnWidth(13, 10 * 256);
            sheet.setColumnWidth(14, 10 * 256);
            sheet.setColumnWidth(15, 10 * 256);
            sheet.setColumnWidth(16, 15 * 256);
            sheet.setColumnWidth(17, 15 * 256);
            sheet.setColumnWidth(18, 15 * 256);
            sheet.setColumnWidth(19, 60 * 256);
            sheet.setColumnWidth(20, 60 * 256);
            sheet.setColumnWidth(21, 60 * 256);
            sheet.setColumnWidth(22, 60 * 256);
            sheet.setColumnWidth(23, 60 * 256);
            sheet.setColumnWidth(24, 60 * 256);
            sheet.setColumnWidth(25, 60 * 256);
            sheet.setColumnWidth(26, 60 * 256);
            sheet.setColumnWidth(27, 60 * 256);
            // 物流品牌
            sheet.setColumnWidth(27, 60 * 256);


            HSSFRow headerRow = sheet.createRow(0);

            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("订单编号");
            headCell0.setCellStyle(headerStyle);

            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("买家会员名");
            headCell1.setCellStyle(headerStyle);

            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("收货人姓名");
            headCell2.setCellStyle(headerStyle);

            HSSFCell headCell3 = headerRow.createCell(3);
            headCell3.setCellValue("收货地址");
            headCell3.setCellStyle(headerStyle);

            HSSFCell headCell4 = headerRow.createCell(4);
            headCell4.setCellValue("联系手机");
            headCell4.setCellStyle(headerStyle);


            HSSFCell headCell5 = headerRow.createCell(5);
            headCell5.setCellValue("商品1Jan Code");
            headCell5.setCellStyle(headerStyle);

            HSSFCell headCell6 = headerRow.createCell(6);
            headCell6.setCellValue("商品1数量");
            headCell6.setCellStyle(headerStyle);

            HSSFCell headCell7 = headerRow.createCell(7);
            headCell7.setCellValue("商品2Jan Code");
            headCell7.setCellStyle(headerStyle);

            HSSFCell headCell8 = headerRow.createCell(8);
            headCell8.setCellValue("商品2数量");
            headCell8.setCellStyle(headerStyle);

            HSSFCell headCell9 = headerRow.createCell(9);
            headCell9.setCellValue("商品3Jan Code");
            headCell9.setCellStyle(headerStyle);

            HSSFCell headCell10 = headerRow.createCell(10);
            headCell10.setCellValue("商品3重量");
            headCell10.setCellStyle(headerStyle);

            HSSFCell headCell11 = headerRow.createCell(11);
            headCell11.setCellValue("商品4Jan Code");
            headCell11.setCellStyle(headerStyle);

            HSSFCell headCell12 = headerRow.createCell(12);
            headCell12.setCellValue("商品4数量");
            headCell12.setCellStyle(headerStyle);

            HSSFCell headCell13 = headerRow.createCell(13);
            headCell13.setCellValue("商品5Jan Code");
            headCell13.setCellStyle(headerStyle);

            HSSFCell headCell14 = headerRow.createCell(14);
            headCell14.setCellValue("商品5数量");
            headCell14.setCellStyle(headerStyle);


            HSSFCell headCell15 = headerRow.createCell(15);
            headCell15.setCellValue("商品6Jan Code");
            headCell15.setCellStyle(headerStyle);


            HSSFCell headCell16 = headerRow.createCell(16);
            headCell16.setCellValue("商品6数量");
            headCell16.setCellStyle(headerStyle);


            HSSFCell headCell17 = headerRow.createCell(17);
            headCell17.setCellValue("商品7Jan Code");
            headCell17.setCellStyle(headerStyle);


            HSSFCell headCell18 = headerRow.createCell(18);
            headCell18.setCellValue("商品7数量");
            headCell18.setCellStyle(headerStyle);

            HSSFCell headCell19 = headerRow.createCell(19);
            headCell19.setCellValue("商品8Jan Code");
            headCell19.setCellStyle(headerStyle);

            HSSFCell headCell20 = headerRow.createCell(20);
            headCell20.setCellValue("商品8数量");
            headCell20.setCellStyle(headerStyle);

            HSSFCell headCell21 = headerRow.createCell(21);
            headCell21.setCellValue("商品9Jan Code");
            headCell21.setCellStyle(headerStyle);

            HSSFCell headCell22 = headerRow.createCell(22);
            headCell22.setCellValue("商品9数量");
            headCell22.setCellStyle(headerStyle);

            HSSFCell headCell23 = headerRow.createCell(23);
            headCell23.setCellValue("商品10Jan Code");
            headCell23.setCellStyle(headerStyle);

            HSSFCell headCell24 = headerRow.createCell(24);
            headCell24.setCellValue("物流单号");
            headCell24.setCellStyle(headerStyle);

            HSSFCell headCell25 = headerRow.createCell(25);
            headCell25.setCellValue("集栈订单编号");
            headCell25.setCellStyle(headerStyle);

            HSSFCell headCell26 = headerRow.createCell(26);
            headCell26.setCellValue("订单状态");
            headCell26.setCellStyle(headerStyle);

            HSSFCell headCell27 = headerRow.createCell(27);
            headCell27.setCellValue("订单时间");
            headCell27.setCellStyle(headerStyle);

            HSSFCell headCell28 = headerRow.createCell(28);
            headCell28.setCellValue("快递公司");
            headCell28.setCellStyle(headerStyle);

            for (int i = 0; i < orderExportList.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                OrderExport orderExport = orderExportList.get(i);

                row.createCell(0).setCellValue(orderExport.getTbOrderId());
                row.createCell(1).setCellValue(orderExport.getBuyerName());
                row.createCell(2).setCellValue(orderExport.getRecvName());
                row.createCell(3).setCellValue(orderExport.getRecvAddr());
                row.createCell(4).setCellValue(orderExport.getRecvMobile());


                int startColumn = 5;
                List<ShopExportVo> shopExportVoList = orderExport.getShopExportVoList();
                for (ShopExportVo shopExportVo : shopExportVoList) {
                    row.createCell(startColumn++).setCellValue(shopExportVo.getJancode());
                    row.createCell(startColumn++).setCellValue(shopExportVo.getQuantity());
                }

                row.createCell(24).setCellValue(orderExport.getExpressNumber());
                row.createCell(25).setCellValue(orderExport.getJizhanOrderId());
                row.createCell(26).setCellValue(orderExport.getOrderStatus());
                row.createCell(27).setCellValue(orderExport.getOrderTime());

                String expressNumber = orderExport.getExpressNumber();
                String expressCompany = StringUtils.isEmpty(expressNumber) ? ""
                        : (expressNumber.startsWith("BS") && expressNumber.endsWith("CN") ? "EMS" : "日本邮政");
                row.createCell(28).setCellValue(expressCompany);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            String dateStr = sdf.format(new Date());

            if (!StringUtils.isEmpty(originFileName)) {
                originFileName = originFileName.substring(0, originFileName.lastIndexOf("."));
            }
            String fileName = originFileName + dateStr + ".xls";

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


	@Override
	public List<OrderBatch> listOrderBatchByOpenId(String openId) {
		return orderBatchRepository.findByOpenId(openId);
	}
	
	
	
	
	
	
	
	

}
