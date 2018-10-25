package com.jizhangyl.application.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jizhangyl.application.enums.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.primary.Shop;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.dataobject.primary.WxuserSender;
import com.jizhangyl.application.dto.QueryOrderDetailReceiverDTO;
import com.jizhangyl.application.dto.QueryOrderDetailShopDTO;
import com.jizhangyl.application.dto.QueryOrderMasterDTO;
import com.jizhangyl.application.dto.SenderDTO;
import com.jizhangyl.application.dto.TraceDTO;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.ExpressQueryHistoryRepository;
import com.jizhangyl.application.repository.primary.OrderDetailRepository;
import com.jizhangyl.application.repository.primary.OrderMasterRepository;
import com.jizhangyl.application.repository.primary.ShopRepository;
import com.jizhangyl.application.repository.secondary.WxuserRepository;
import com.jizhangyl.application.repository.primary.WxuserSenderRepository;
import com.jizhangyl.application.service.QueryOrderService;
import com.jizhangyl.application.service.WxuserAddrService;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.EMSUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年9月26日 上午9:49:17
 * @description QueryOrderService实现类
 */
@Slf4j
@Service
public class QueryOrderServiceImpl implements QueryOrderService {

    @Autowired
    private OrderMasterRepository orderMasterRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private WxuserAddrService wxuserAddrService;
    
    @Autowired
    private WxuserRepository wxuserRepository;
    
    @Autowired
    private WxuserSenderRepository wxuserSenderRepository;
    
    @Autowired
    private ExpressQueryHistoryRepository expressQueryHistoryRepository;
    
    @Autowired
    private EMSUtil emsUtil;
    
    /**
     * 根据"物流单号,航班提单号,买手邀请码,收件人,电话"以及"订单状态"查询订单列表
     * @param content 物流单号,航班提单号,买手邀请码
     * @param status 订单状态
     * @return 满足条件的订单列表
     */
    @Override
    public List<QueryOrderMasterDTO> findByExpressDeliveryInviteCodeStatus(String content, String status, String startTimeStr, String endTimeStr, Integer page, Integer size) {
        List<QueryOrderMasterDTO> resultList = new LinkedList<QueryOrderMasterDTO>();

        // 1.查询出所有订单(有日期限制的话就在日期限制的范围内查询)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OrderMaster> orderMasterList = new ArrayList<OrderMaster>();
        try {
            Integer st = Integer.valueOf(status);
            if (st != null && st.intValue() != OrderStatusEnum.FIND_ALL.getCode().intValue()) { // 按照状态进行筛选
                if (!StringUtils.isEmpty(startTimeStr) && !StringUtils.isEmpty(endTimeStr)) {
                    orderMasterList = orderMasterRepository.findByOrderStatusAndCreateTimeBetween(Integer.valueOf(status), sdf.parse(startTimeStr), sdf.parse(endTimeStr));
                } else {
                    orderMasterList = orderMasterRepository.findByOrderStatus(Integer.valueOf(status));
                }
            } else { // 查询所有
                if (!StringUtils.isEmpty(startTimeStr) && !StringUtils.isEmpty(endTimeStr)) {
                    orderMasterList = orderMasterRepository.findByCreateTimeBetween(sdf.parse(startTimeStr), sdf.parse(endTimeStr));
                } else {
                    orderMasterList = orderMasterRepository.findAll();
                }
            }
        } catch (ParseException e) {
            log.info("【日期格式化错误】");
            throw new GlobalException(ResultEnum.DATE_FORMAT_ERROR);
        }

        // 2.查询出所有的微信用户
        List<Wxuser> wxuserList = wxuserRepository.findAll();

        
        // 查询出满足条件的发件人(新增按照发件人查询)
        List<Integer> idList = orderMasterList.stream().map(e->e.getBuyerAddrId()).collect(Collectors.toList());
        List<WxuserSender> senderList = wxuserSenderRepository.findByIdIn(idList);
        Map<Integer, WxuserSender> senderMap = new HashMap<Integer, WxuserSender>();
        for (WxuserSender wxuserSender : senderList) {
            senderMap.put(wxuserSender.getId(), wxuserSender);
        }
        
        // 3.封装一个key为openid,value为Wxuser的map,用于根据买手的openid获取其对应的邀请码(inviteCode)
        Map<String, Wxuser> openidWxuser = new HashMap<String, Wxuser>(1024);
        for (Wxuser wxuser : wxuserList) {
            openidWxuser.put(wxuser.getOpenId(), wxuser);
        }
        
        // 4.按照订单状态进行一次筛选,之后把一次筛选后的OrderMaster列表组装成QueryOrderMasterDTO列表,准备二次筛选
        try {
            Integer st = Integer.parseInt(status);
            /*if (st.intValue() != OrderStatusEnum.FIND_ALL.getCode().intValue()) { // 不查询所有的情况下才进行筛选
                orderMasterList = orderMasterList.stream().filter(e->
                    e.getOrderStatus().equals(st.intValue())
                ).collect(Collectors.toList());
            }*/
            
            // 组装
            for (OrderMaster orderMaster : orderMasterList) {
                QueryOrderMasterDTO orderMasterDTO = new QueryOrderMasterDTO();
                // 4.1.设置订单时间
                orderMasterDTO.setCreateTime(sdf.format(orderMaster.getCreateTime()));
                // 4.2.设置订单状态(中文)
                switch(orderMaster.getOrderStatus().intValue()) {
                    case 0: orderMasterDTO.setStatus(OrderStatusEnum.NEW.getMsg()); break;
                    case 1: orderMasterDTO.setStatus(OrderStatusEnum.PAID.getMsg()); break;
                    case 2: orderMasterDTO.setStatus(OrderStatusEnum.DELIVERED.getMsg()); break;
                    case 3: orderMasterDTO.setStatus(OrderStatusEnum.RECEIVED.getMsg()); break;
                    case 4: orderMasterDTO.setStatus(OrderStatusEnum.CANCELED.getMsg()); break;
                }
                // 4.3.设置物流单号
                orderMasterDTO.setExpressNum(orderMaster.getExpressNumber() == null ? "" : orderMaster.getExpressNumber());
                
                // 4.4.设置航班提运单号
                orderMasterDTO.setDeliveryNum(orderMaster.getDeliveryNumber() == null ? "" : orderMaster.getDeliveryNumber());
                // 4.5.设置买手邀请码
                String tempOpenId = orderMaster.getBuyerOpenid();
                Wxuser tempWxuser = openidWxuser.get(tempOpenId);
                String tempInviteCode = null;
                if (tempWxuser != null) {
                    tempInviteCode = tempWxuser.getInviteCode();
                }
                orderMasterDTO.setInviteCode(tempInviteCode);
                
                /**
                 * 慎用下面这种链式调用的写法,及其容易发生空指针异常,出了错还不好排查
                 */
//                orderMasterDTO.setInviteCode(openidWxuser.get(orderMaster.getBuyerOpenid()).getInviteCode());
                
                // 4.6.设置买手名称
                orderMasterDTO.setBuyerName(orderMaster.getBuyerName());
                WxuserSender wxuserSender = senderMap.get(orderMaster.getBuyerAddrId()); //wxuserSenderRepository.findOne(orderMaster.getBuyerAddrId());
                if (wxuserSender != null) {
                    // 设置发件人名称
                    orderMasterDTO.setSenderName(wxuserSender.getSender());
                    // 设置发件人电话
                    orderMasterDTO.setSenderPhone(wxuserSender.getPhone());
                }
                // 4.7.设置货值+关税
                orderMasterDTO.setCostAndTaxes(orderMaster.getOrderCost().add(orderMaster.getOrderTaxes()));
                // 4.8.设置运费
                orderMasterDTO.setFreight(orderMaster.getOrderFreight());
                // 4.9.设置订单id(orderId)
                orderMasterDTO.setOrderId(orderMaster.getOrderId());
                // 设置收件人
                orderMasterDTO.setRecipient(orderMaster.getRecipient());
                // 设置收件人电话号码
                orderMasterDTO.setRecipientPhone(orderMaster.getRecipientPhone());
                // 4.10.设置订单的发货时间
                if (orderMaster.getDeliveryTime() != null) {
                    orderMasterDTO.setDeliveryTime(sdf.format(orderMaster.getDeliveryTime()));
                } else {
                    orderMasterDTO.setDeliveryTime("");
                }
                // 4.11.添加到结果集
                resultList.add(orderMasterDTO);
            }
        } catch (NumberFormatException e) {
            log.info("【订单状态数字格式化错误】-->" + status);
            throw new GlobalException(ResultEnum.NUM_FIELD_FORMAT_ERROR);
        }
        
        // 5.根据"物流单号(expressNum),航班提单号(deliveryNum),买手邀请码(inviteCode),收件人(新增),收件人电话号码(新增)"进行二次筛选
        // 新增了三个条件:买手名称(buyerName),发件人名称(senderName),发件人电话(senderPhone)
        if (!StringUtils.isEmpty(content)) { // 查询条件不为空的情况才进行筛选
            for (Iterator<QueryOrderMasterDTO> iterator = resultList.iterator(); iterator.hasNext();) {
                QueryOrderMasterDTO orderMasterDTO = iterator.next();
                // 将既不匹配物流单号,也不匹配航班提单号,也不匹配买手邀请码,也不匹配收件人名称,也不匹配收件人电话的订单移除
                // 也不匹配买手名称,也不匹配发件人名,也不匹配发件人电话-2018-10-10(新增)
                if (
                    (orderMasterDTO.getExpressNum() != null && orderMasterDTO.getExpressNum().contains(content)) ||
                    (orderMasterDTO.getDeliveryNum() != null && orderMasterDTO.getDeliveryNum().contains(content)) ||
                    (orderMasterDTO.getInviteCode() != null && orderMasterDTO.getInviteCode().contains(content)) ||
                    (orderMasterDTO.getBuyerName() != null && orderMasterDTO.getBuyerName().contains(content)) ||
                    (orderMasterDTO.getSenderName() != null && orderMasterDTO.getSenderName().contains(content)) ||
                    (orderMasterDTO.getSenderPhone() != null && orderMasterDTO.getSenderPhone().contains(content)) ||
                    (orderMasterDTO.getRecipient() != null && orderMasterDTO.getRecipient().contains(content)) ||
                    (orderMasterDTO.getRecipientPhone() != null && orderMasterDTO.getRecipientPhone().contains(content))
                    ) {
                    // DO NOTHING
                } else {
                    iterator.remove();
                }
            }
        }
        
        // 6.按照时间排序(近->远)
        Collections.sort(resultList, new Comparator<QueryOrderMasterDTO>(){
            @Override
            public int compare(QueryOrderMasterDTO o1, QueryOrderMasterDTO o2) {
                try {
                    Date d1 = sdf.parse(o1.getCreateTime());
                    Date d2 = sdf.parse(o2.getCreateTime());
                    if (d1.getTime() > d2.getTime()) {
                        return -1;
                    } else if (d1.getTime() < d2.getTime()) {
                        return 1;
                    }
                } catch(ParseException e) {
                    throw new GlobalException(ResultEnum.QUERY_DATE_FORMAT_ERROR);
                }
                return 0;
            }
        });
        
        
        
        List<QueryOrderMasterDTO> subList = resultList.subList((page - 1) * size, page * size > resultList.size() ? resultList.size() : page * size);
        
        // 添加:物流状态->取出每一个物流状态最新的一条记录(最后分完页了再去查)
        /*List<String> expNums = subList.stream().map(e->e.getExpressNum()).filter(e->e != null).collect(Collectors.toList());
        List<ExpressQueryHistory> expressQueryHistoryList = expressQueryHistoryRepository.findByExpNumIn(expNums);
        List<ExpressPreviewVo> expressPreviewVoList = expressQueryHistoryList.stream().map(e -> {
            ExpressPreviewVo expressPreviewVo = new ExpressPreviewVo();
            BeanUtils.copyProperties(e, expressPreviewVo);
            return expressPreviewVo;
        }).collect(Collectors.toList());
        for (ExpressPreviewVo expressPreviewVo : expressPreviewVoList) {
            JSONObject record = emsUtil.query(expressPreviewVo.getExpNum());
            if (record != null) {
                JSONArray jsonArray = record.getJSONArray("traces");
                if (jsonArray != null && jsonArray.size() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.size() - 1);
                    expressPreviewVo.setExpLastRecord(jsonObject);
                } else {
                    expressPreviewVo.setExpLastRecord(null);
                }
            }
        }
        Map<String, ExpressPreviewVo> expMap = new HashMap<String, ExpressPreviewVo>();
        for (ExpressPreviewVo expressPreviewVo : expressPreviewVoList) {
            expMap.put(expressPreviewVo.getExpNum(), expressPreviewVo);
        }*/
        
        // 截出指定页码的list之后,最后去EMS查指定的物流单号的状态,再单独组装物流跟踪信息
        /*for (QueryOrderMasterDTO queryOrderMasterDTO : subList) {
            
            ExpressPreviewVo expVo = expMap.get(queryOrderMasterDTO.getExpressNum());
            if (expVo != null && expVo.getExpLastRecord() != null) {
                // 将字符串解析成物流对象
                TraceDTO traceDTO = JSON.parseObject(expVo.getExpLastRecord().toString(), TraceDTO.class);
                // 物流状态字符串
                StringBuilder logistics = new StringBuilder();
                
                String acceptTime = traceDTO.getAcceptTime();
                Date accDate = DateUtil.StringToDate(acceptTime);
                SimpleDateFormat accSdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                acceptTime = accSdf.format(accDate);
                String[] accs = acceptTime.split(" ");
                accs[1] = DateUtil.getStatus(traceDTO.getAcceptTime()).getMsg() + " " + accs[1];
                acceptTime = accs[0] + " " + accs[1];
                
                logistics.append(acceptTime);
                logistics.append("[");
                logistics.append(traceDTO.getAcceptAddress());
                logistics.append("]");
                logistics.append(traceDTO.getRemark());
                queryOrderMasterDTO.setExpressStatus(logistics.toString());
            } else {
                queryOrderMasterDTO.setExpressStatus(null);
            }
        }*/
        
        // 6.返回
        return resultList;
    }

    @Override
    public List<String> findLogisticsStatus(String expNum) {
        if (StringUtils.isEmpty(expNum)) {
            log.info("【待查询的物流单号为空】");
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        
        List<String> traces = new ArrayList<String>(16);
        JSONObject record = emsUtil.query(expNum);
        if (record != null) {
            JSONArray jsonArray = record.getJSONArray("traces");
            if (jsonArray != null && jsonArray.size() > 0) {
                for (int i = jsonArray.size() - 1; i >= 0; i--) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // 将字符串解析成物流对象
                    TraceDTO traceDTO = JSON.parseObject(jsonObject.toString(), TraceDTO.class);
                    // 物流状态字符串
                    StringBuilder logistics = new StringBuilder();
                    
                    String acceptTime = traceDTO.getAcceptTime();
                    Date accDate = DateUtil.StringToDate(acceptTime);
                    SimpleDateFormat accSdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    acceptTime = accSdf.format(accDate);
                    String[] accs = acceptTime.split(" ");
                    accs[1] = DateUtil.getStatus(traceDTO.getAcceptTime()).getMsg() + " " + accs[1];
                    acceptTime = accs[0] + " " + accs[1];
                    
                    logistics.append(acceptTime);
                    logistics.append("【");
                    logistics.append(traceDTO.getAcceptAddress());
                    logistics.append("】");
                    logistics.append(traceDTO.getRemark());
                    traces.add(logistics.toString());
                }
                return traces;
            } else {
                return traces;
            }
        }
        return traces;
    }
    
    /**
     * 根据订单id查询该订单内的所有商品详情
     * @param orderId 订单id
     * @return 该订单包含的商品详情列表(Jan code,img,商品标题,售价,关税,数量)
     */
    @Override
    public List<QueryOrderDetailShopDTO> findOrderDetailByOrderId(String orderId) {
        List<QueryOrderDetailShopDTO> result = new LinkedList<QueryOrderDetailShopDTO>();
        
        // 1.查询该订单内的所有商品详情
        List<OrderDetail> detailList = orderDetailRepository.findByOrderId(orderId);
        
        // 2.查询出所有的商品信息,并将productId作为key,shop作为value组成一个map
        List<Shop> shopList = shopRepository.findAll();
        Map<String, Shop> shopMap = new HashMap<String, Shop>(512);
        for (Shop shop : shopList) {
            shopMap.put(shop.getId().toString(), shop);
        }
        
        // 3.把所有的商品详情组成所需的数据结构
        for (OrderDetail orderDetail : detailList) {
            QueryOrderDetailShopDTO orderDetailDTO = new QueryOrderDetailShopDTO();
            // 3.1.设置商品Jancode
            orderDetailDTO.setJancode(shopMap.get(orderDetail.getProductId()).getShopJan());
            // 3.2.设置商品的img
            orderDetailDTO.setImg(shopMap.get(orderDetail.getProductId()).getShopImage());
            // 3.3.设置商品标题
            orderDetailDTO.setProductName(orderDetail.getProductName());
            // 3.4.设置售价
            orderDetailDTO.setCost(orderDetail.getProductPrice());
            // 3.5.设置关税
            orderDetailDTO.setTaxes(orderDetail.getDetailTaxes());
            // 3.6.设置数量
            orderDetailDTO.setNum(orderDetail.getProductQuantity());
            result.add(orderDetailDTO);
        }
        
        return result;
    }

    /**
     * 根据订单id查询收件人信息
     * @param orderId 订单id
     * @return 该订单的收件人信息(收件人名称,收件人电话,收件人详细地址)
     */
    @Override
    public List<QueryOrderDetailReceiverDTO> findOrderReceiverByOrderId(String orderId) {
        List<QueryOrderDetailReceiverDTO> receiverList = new ArrayList<QueryOrderDetailReceiverDTO>();
        
        // 1.根据orderId查询出该订单的收件人姓名,收件人电话,收件人地址id
        List<OrderMaster> orderList = orderMasterRepository.findByOrderId(orderId);
        
        // 2.查询出所有的微信用户的地址,并把id作为key,wxuserAddr作为value组成一个map
        for (OrderMaster orderMaster : orderList) {
            QueryOrderDetailReceiverDTO receiver = new QueryOrderDetailReceiverDTO();
            // 2.1.设置收件人名称
            receiver.setRecipient(orderMaster.getRecipient());
            
            // 2.2.设置收件人电话
            receiver.setRecipientPhone(orderMaster.getRecipientPhone());
            
            // 2.3.设置收件人详细地址
            StringBuilder addr = new StringBuilder();
            WxuserAddr wxuserAddr = wxuserAddrService.findOne(orderMaster.getRecipientAddrId());
            addr.append(wxuserAddr.getProvince());
            addr.append(wxuserAddr.getCity());
            addr.append(wxuserAddr.getArea());
            addr.append(wxuserAddr.getDetailAddr());
            receiver.setRecipientAddr(addr.toString());;
            
            receiverList.add(receiver);
        }
        return receiverList;
    }

    /**
     * 根据orderId查出发件人信息
     * @param orderId 订单id
     * @return 该订单的发件人信息
     */
    @Override
    public List<SenderDTO> findOrderSenderByOrderId(String orderId) {
        List<SenderDTO> senderList = new ArrayList<SenderDTO>(1);
        // 1.根据订单id查出订单的信息
        OrderMaster orderMaster = orderMasterRepository.findOne(orderId);
        // 2.根据订单信息中的buyerAddrId查出发件人的相关信息
        WxuserSender wxuserSender = wxuserSenderRepository.findOne(orderMaster.getBuyerAddrId());
        // 3.找到发件人信息中的发件人名称和发件人电话
        SenderDTO senderDTO = new SenderDTO();
        if (wxuserSender != null) {
            senderDTO.setSenderPhone(wxuserSender.getPhone());
            senderDTO.setSenderName(wxuserSender.getSender());
        }
        
        senderList.add(senderDTO);
        return senderList;
    }
}
