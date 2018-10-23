package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.converter.OrderMaster2OrderDtoConverter;
import com.jizhangyl.application.dataobject.ExpressNumJp;
import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dataobject.Shop;
import com.jizhangyl.application.dataobject.Wxuser;
import com.jizhangyl.application.dataobject.WxuserAddr;
import com.jizhangyl.application.dataobject.WxuserSender;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.PayStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.OrderDetailRepository;
import com.jizhangyl.application.repository.OrderMasterRepository;
import com.jizhangyl.application.service.CartService;
import com.jizhangyl.application.service.ExpenseCalendarService;
import com.jizhangyl.application.service.ExpressNumJpService;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.PayService;
import com.jizhangyl.application.service.RedisLock;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.service.WxuserAddrService;
import com.jizhangyl.application.service.WxuserSenderService;
import com.jizhangyl.application.service.WxuserService;
import com.jizhangyl.application.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/10 14:16
 * @description 订单主表业务实现主类
 */
@Slf4j
@Service
public class OrderMasterServiceImpl implements OrderMasterService {

    // 35 元 1 公斤
    private static final double EXPRESS_UNIT = 35;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ShopService shopService;

    @Autowired
    private WxuserService wxuserService;

    @Autowired
    private WxuserSenderService wxuserSenderService;

    @Autowired
    private WxuserAddrService wxuserAddrService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PayService payService;

    @Autowired
    private ExpressNumJpService expressNumJpService;

    @Autowired
    private ExpenseCalendarService expenseCalendarService;

    @Autowired
    private RedisLock redisLock;

    private static final int TIMEOUT = 10 * 1000;

    @Override
    public OrderDto findOne(String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        OrderMaster orderMaster = orderMasterRepository.findOne(orderId);
        if (orderMaster == null) {
            throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
        }
        OrderDto orderDto = OrderMaster2OrderDtoConverter.convert(orderMaster);

        // 订单详情
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);

        orderDto.setOrderDetailList(orderDetailList);

        return orderDto;
    }

    /**
     * 删除订单信息
     *
     * @param orderId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        OrderMaster orderMaster = orderMasterRepository.findOne(orderId);
        if (orderMaster == null) {
            throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
        }
        // 查询出订单详情表对应的数据
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);

        if (!CollectionUtils.isEmpty(orderDetailList)) {
            orderDetailRepository.delete(orderDetailList);
        }

        orderMasterRepository.delete(orderMaster);

    }

    @Override
    public Page<OrderDto> findAll(Pageable pageable) {
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);
        List<OrderDto> orderDtoList = OrderMaster2OrderDtoConverter.convert(orderMasterPage.getContent());
        if (CollectionUtils.isEmpty(orderDtoList)) {
            orderDtoList.stream().filter(e -> {
                String orderId = e.getOrderId();
                List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
                e.setOrderDetailList(orderDetailList);
                return true;
            }).collect(Collectors.toList());
        }
        return new PageImpl<>(orderDtoList, pageable, orderMasterPage.getTotalElements());
    }

    /**
     * 订单创建
     *
     * @param orderDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderDto create(OrderDto orderDto, boolean fromFile) {
        if (orderDto == null) {
            throw new GlobalException(ResultEnum.PARAM_ERROR);
        }
        String orderId = KeyUtil.genUniqueKey();
        // 订单总价
        BigDecimal orderAmount = BigDecimal.ZERO;
        // 报关总税金
        BigDecimal orderTaxes = BigDecimal.ZERO;
        // 总运费
        BigDecimal orderFreight = BigDecimal.ZERO;
        // 总货值
        BigDecimal orderCost = BigDecimal.ZERO;

        BigDecimal orderNetWeight = BigDecimal.ZERO;
        BigDecimal orderPackWeight = BigDecimal.ZERO;
        BigDecimal orderTotalVolume = BigDecimal.ZERO;

        // 订单详情入库
        List<OrderDetail> orderCartList = orderDto.getOrderDetailList();
        List<Integer> productIdList = orderCartList.stream().map(
                e -> Integer.parseInt(e.getProductId()))
                .collect(Collectors.toList());

        // 此处开始加锁
        long time = System.currentTimeMillis() + TIMEOUT;
        if (!redisLock.lock(this.getClass(), String.valueOf(time))) {
            throw new GlobalException(ResultEnum.ORDER_CREATE_ERROR);
        }

        List<Shop> shopList = shopService.findByIdIn(productIdList);
        List<OrderDetail> orderDetailList = new ArrayList<>();
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
                // 报关售价
                BigDecimal bcPrice = shop.getBcPrice();
                // 供货价
                BigDecimal gPrice = shop.getShopGprice();
                // 报关税率
                Double bcCess = shop.getBcCess();

                double netWeight = ((double) (shop.getShopJweight())) / 1000;
                double packWeight = ((double) (shop.getShopDweight())) / 1000;

                String volume = shop.getShopVolume();

                Integer productQuantity = od.getProductQuantity();
                // 判断库存是否足够
                Integer stock = shop.getShopCount() - productQuantity;
                if (stock < 0) {
                    log.error("【订单入库】商品库存不足, Jancode = {}", shop.getShopJan());
                    throw new GlobalException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH.getCode(), "商品库存不足, Jancode = " + shop.getShopJan());
                }

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setDetailId(KeyUtil.genUniqueKey());
                orderDetail.setOrderId(orderId);
                orderDetail.setProductId(String.valueOf(od.getProductId()));
                orderDetail.setProductName(shop.getShopName());

                orderDetail.setProductPrice(productPrice);
                orderDetail.setProductQuantity(productQuantity);

                // 类型的数量转换为 BigDecimal 类型
                BigDecimal bdProductQuantity = new BigDecimal(productQuantity);

                // 计算总货值
                orderCost = productPrice.multiply(bdProductQuantity).add(orderCost);
                // 1件商品的税金 （供货价 * 报关税率）
                BigDecimal oneTaxes = gPrice.multiply(new BigDecimal(bcCess).divide(new BigDecimal(100)));

                // 计算订单详情的总税金：报关售价 * 报关税率 * 商品数量
                BigDecimal detailTaxes = oneTaxes.multiply(bdProductQuantity);
                orderTaxes = detailTaxes.add(orderTaxes);

                // 计算总净重
                orderNetWeight = new BigDecimal(netWeight).multiply(bdProductQuantity).add(orderNetWeight);

                // 计算总打包重量
                orderPackWeight = new BigDecimal(packWeight).multiply(bdProductQuantity).add(orderPackWeight);

                // 计算总体积
                orderTotalVolume = new BigDecimal(volume).multiply(new BigDecimal(productQuantity)).add(orderTotalVolume);


                // 设置订单详情页商品图片
                orderDetail.setProductIcon(shop.getShopImage());

                // 设置订单详情的税金
                orderDetail.setDetailTaxes(detailTaxes);

                orderDetailList.add(orderDetail);
            }
        }

        // 订单详情统一入库
        orderDetailRepository.save(orderDetailList);
        // 订单统一扣库存

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shopService.decreaseStock(orderDetailList);

        // 订单主表信息入库
        OrderMaster orderMaster = new OrderMaster();

        // 代购的 openid
        String openid = orderDto.getBuyerOpenid();

        // 清空购物车
        if (!fromFile) {
            cartService.clearCart(openid, orderDetailList);
        }

        orderMaster.setOrderId(orderId);

        double calcWeight = computeWeightRound(orderPackWeight.doubleValue());
        BigDecimal calcFreight = new BigDecimal(calcWeight).multiply(new BigDecimal(EXPRESS_UNIT));

        // 设置运费
        orderMaster.setOrderFreight(calcFreight);

        // 设置实收货值
        orderMaster.setOrderCost(orderCost);

        // 设置实收税金
        orderMaster.setOrderTaxes(orderTaxes);

        // 设置总金额
        orderAmount = orderCost.add(calcFreight).add(orderTaxes);
        orderMaster.setOrderAmount(orderAmount);

        Wxuser wxuser = wxuserService.findByOpenId(openid);

        orderMaster.setBuyerName(wxuser.getNickName());

        Integer senderAddrId = Integer.valueOf(orderDto.getBuyerAddrId());
        Integer addrId = orderDto.getRecipientAddrId();
        WxuserSender wxuserSender = wxuserSenderService.findOne(senderAddrId);
        WxuserAddr wxuserAddr = wxuserAddrService.findOne(addrId);

        orderMaster.setBuyerPhone(wxuserSender.getPhone());
        orderMaster.setBuyerAddrId(senderAddrId);
        orderMaster.setBuyerOpenid(openid);

        orderMaster.setRecipient(wxuserAddr.getReceiver());
//        orderMaster.setRecipientIdCard(wxuserAddr.);
        orderMaster.setRecipientPhone(wxuserAddr.getPhone());
        orderMaster.setRecipientAddrId(addrId);
        orderMaster.setNetWeight(orderNetWeight);

        orderMaster.setPackWeight(orderPackWeight);
        orderMaster.setTotalVolume(orderTotalVolume);

        if (!StringUtils.isEmpty(orderDto.getBuyerOrderId())) {
            orderMaster.setBuyerOrderId(orderDto.getBuyerOrderId());
        }

        if (!StringUtils.isEmpty(orderDto.getCustomerName())) {
            orderMaster.setCustomerName(orderDto.getCustomerName());
        }

        OrderMaster resultMaster = orderMasterRepository.save(orderMaster);

        // 解锁
        redisLock.unlock(this.getClass(), String.valueOf(time));

        OrderDto resultDto = OrderMaster2OrderDtoConverter.convert(resultMaster);
        return resultDto;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderDto paid(OrderDto orderDto) {
        // 判断订单状态
        if (!orderDto.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【订单支付完成】订单状态不正确, orderId = {}, orderStatus = {}", orderDto.getOrderId(), orderDto.getOrderStatus());
            throw new GlobalException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // 判断支付状态
        if (!orderDto.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) {
            log.error("【微信支付完成】支付状态不正确, orderDto = {}", orderDto);
            throw new GlobalException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        // 修改订单状态
        orderDto.setOrderStatus(OrderStatusEnum.PAID.getCode());
        // 修改付款状态
        orderDto.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        // 设置付款时间
        orderDto.setPayTime(new Date());

        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDto, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);

        if (updateResult == null) {
            log.error("【订单支付完成】更新失败, orderMaster = {}", orderMaster);
            throw new GlobalException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDto;
    }

    /**
     * 根据 openid 和订单状态查询订单
     *
     * @param openid
     * @param orderStatus
     * @return
     */
    @Override
    public List<OrderDto> findByBuyerOpenidAndOrderStatus(String openid, Integer orderStatus,
                                                          Integer page, Integer size) {
        if (StringUtils.isEmpty(openid) || orderStatus == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        List<OrderDto> orderDtoList = new ArrayList<>();
        try {
            List<Integer> orderStatusList = new ArrayList<Integer>(5) {
                {
                    add(OrderStatusEnum.NEW.getCode());
                    add(OrderStatusEnum.PAID.getCode());
                    add(OrderStatusEnum.DELIVERED.getCode());
                    add(OrderStatusEnum.RECEIVED.getCode());
                    add(OrderStatusEnum.CANCELED.getCode());
                }
            };
            if (!orderStatus.equals(OrderStatusEnum.FIND_ALL.getCode())) {
                orderStatusList.clear();
                orderStatusList.add(orderStatus);
            }

            PageRequest pageRequest = new PageRequest(page - 1, size);
            Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenidAndOrderStatusInOrderByCreateTimeDesc(openid,
                    orderStatusList, pageRequest);

            List<OrderMaster> orderMasterList = orderMasterPage.getContent();

            List<String> orderIdList = orderMasterList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
            List<OrderDetail> detailList = orderDetailRepository.findByOrderIdIn(orderIdList);

            List<Integer> shopIdList = detailList.stream().map(e -> Integer.parseInt(e.getProductId())).collect(Collectors.toList());
            List<Shop> shopList = shopService.findByIdIn(shopIdList);

            // 转换为 orderDtoList
            // 返回前端添加"税金"和"图片"
            orderDtoList = orderMasterList.stream().map(e -> {
                OrderDto orderDto = new OrderDto();
                BeanUtils.copyProperties(e, orderDto);

                List<OrderDetail> orderDetailList = new ArrayList<>();

                for (OrderDetail orderDetail : detailList) {
                    if (e.getOrderId().equals(orderDetail.getOrderId())) {
                        orderDetailList.add(orderDetail);
                    }
                }

                orderDetailList = orderDetailList.stream().map(ele -> {
                    // TODO 这一块逻辑后续需要迁移到订单详情入库时候操作
                    String productId = ele.getProductId();
                    Shop shop = null;
                    for (Shop s : shopList) {
                        if (productId.equals(String.valueOf(s.getId()))) {
                            shop = s;
                            break;
                        }
                    }

                    ele.setProductIcon(shop.getShopImage());

                    // 供货价
                    BigDecimal gPrice = shop.getShopGprice();
                    // 报关价格
                    BigDecimal bcPrice = shop.getBcPrice();
                    // 报关税率
                    BigDecimal bcCess = new BigDecimal(shop.getBcCess()).divide(new BigDecimal(100));

                    // 商品数量
                    BigDecimal quantity = new BigDecimal(ele.getProductQuantity());
                    // 设置订单详情的税金
//                    BigDecimal detailTaxes = gPrice.multiply(bcCess).multiply(quantity);
                    BigDecimal detailTaxes = gPrice.multiply(bcCess);
                    ele.setDetailTaxes(detailTaxes);
                    return ele;
                }).collect(Collectors.toList());
                orderDto.setOrderDetailList(orderDetailList);
                return orderDto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("【查询订单】发生异常: {}", e.getMessage());
            e.printStackTrace();
            throw new GlobalException(ResultEnum.SERVER_ERROR.getCode(), "【查询订单】发生异常: " + e.getMessage());
        }

        return orderDtoList;
    }

    /**
     * 销售额计算
     *
     * @param openid
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public Map<String, String> sellAmount(String openid, String beginDate, String endDate) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            // 日期格式化
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date beginTime = sdf.parse(beginDate);
            Date endTime = sdf.parse(endDate);

            // 设置结束时间为结束日期的后一天才能查询到包括最后一天的数据
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            endTime = calendar.getTime();

            if (endTime.getTime() < beginTime.getTime()) {
                throw new GlobalException(ResultEnum.QUERY_DATE_ERROR);
            }
            // 查出该 openid 下所有订单, 状态为已经 OrderStatusEnum.PAID 的所有订单
            List<OrderMaster> orderMasterList = orderMasterRepository.findByBuyerOpenid(openid, beginTime, endTime);
            if (!CollectionUtils.isEmpty(orderMasterList)) {
                BigDecimal totalCost = BigDecimal.ZERO;
                BigDecimal totalTaxes = BigDecimal.ZERO;
                BigDecimal totalFreight = BigDecimal.ZERO;
                for (OrderMaster orderMaster : orderMasterList) {
                    if (orderMaster.getOrderStatus().equals(OrderStatusEnum.PAID.getCode()) ||
                            orderMaster.getOrderStatus().equals(OrderStatusEnum.DELIVERED.getCode()) ||
                            orderMaster.getOrderStatus().equals(OrderStatusEnum.RECEIVED.getCode())) {
                        totalCost = totalCost.add(orderMaster.getOrderCost());
                        totalTaxes = totalTaxes.add(orderMaster.getOrderTaxes());
                        totalFreight = totalFreight.add(orderMaster.getOrderFreight());
                    }
                }
                resultMap.put("totalCost", totalCost.toString());
                resultMap.put("totalTaxes", totalTaxes.toString());
                resultMap.put("totalFreight", totalFreight.toString());

                BigDecimal nowExpenseSum = expenseCalendarService.getNowExpenseSum(openid);
                resultMap.put("nowExpenseSum", nowExpenseSum.toString());
            } else {
                resultMap.put("totalCost", "0");
                resultMap.put("totalTaxes", "0");
                resultMap.put("totalFreight", "0");
                resultMap.put("nowExpenseSum", "0");
            }

        } catch (ParseException e) {
            log.error("【查询销售额】参数有误: {}", ResultEnum.QUERY_DATE_FORMAT_ERROR.getMessage());
            e.printStackTrace();
            throw new GlobalException(ResultEnum.QUERY_DATE_FORMAT_ERROR);
        }

        return resultMap;
    }

    /**
     * 取消订单
     *
     * @param orderDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderDto cancel(OrderDto orderDto) {
        // 1. 判断订单状态
        if (!(orderDto.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())
                || orderDto.getOrderStatus().equals(OrderStatusEnum.PAID.getCode()))) {
            log.error("【取消订单】订单状态不正确, orderId = {}, orderStatus = {}", orderDto.getOrderId(), orderDto.getOrderStatus());
            throw new GlobalException(ResultEnum.ORDER_STATUS_ERROR);
        }

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

        // 4. 归还物流单号
        String expNum = orderDto.getExpressNumber();
        ExpressNumJp expressNumJp = expressNumJpService.findByExpNum(expNum);
        if (expressNumJp != null) {
            // 此步实现单号排序，先将该条单号记录删除，然后新增到最后一条
            // 删除该单号的记录
            expressNumJpService.delete(expressNumJp.getId());

            // 插入到最后一条记录
            ExpressNumJp record = new ExpressNumJp();
            record.setExpNum(expressNumJp.getExpNum());
            expressNumJpService.save(record);
        }

        // 6. 如果已支付，需要退款
        if (orderDto.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {
            payService.refund(orderDto);
        }
        return orderDto;
    }

    @Override
    public List<OrderMaster> findByOrderIdIn(List<String> orderIdList) {
        return orderMasterRepository.findByOrderIdIn(orderIdList);
    }

    @Override
    public List<OrderMaster> findByOrderStatusAndDeliveryTimeBetween(Integer orderStatus, Date startTime, Date endTime) {
        return orderMasterRepository.findByOrderStatusAndDeliveryTimeBetween(orderStatus, startTime, endTime);
    }

    @Override
    public void delivery(String orderId) {
        try {
            OrderMaster orderMaster = findOne(orderId);
            if (orderMaster == null) {
                throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
            }
            if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {
                throw new GlobalException(ResultEnum.ORDER_STATUS_ERROR.getCode(),
                        ResultEnum.ORDER_STATUS_ERROR.getMessage() + " 当前状态: " + orderMaster.getOrderStatusEnum().getMsg());
            }
            orderMaster.setOrderStatus(OrderStatusEnum.DELIVERED.getCode());
            orderMaster.setDeliveryTime(new Date());

            orderMasterRepository.save(orderMaster);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResultEnum.DELIVERY_ERROR);
        }
    }

    /**
     * 批量发货
     *
     * @param orderMasterList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delivery(List<OrderMaster> orderMasterList) {

        try {
            for (OrderMaster orderMaster : orderMasterList) {
                if (orderMaster == null) {
                    throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
                }
                if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {
                    throw new GlobalException(ResultEnum.ORDER_STATUS_ERROR.getCode(),
                            ResultEnum.ORDER_STATUS_ERROR.getMessage() + " 当前状态: " + orderMaster.getOrderStatusEnum().getMsg()
                                    + "，单号：" + orderMaster.getExpressNumber());
                }
                orderMaster.setOrderStatus(OrderStatusEnum.DELIVERED.getCode());
                orderMaster.setDeliveryTime(new Date());
            }

            orderMasterRepository.save(orderMasterList);

        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResultEnum.DELIVERY_ERROR);
        }
    }

    @Override
    public List<OrderMaster> findByExpressNumber(String expressNumber) {
        return orderMasterRepository.findByExpressNumber(expressNumber);
    }

    @Override
    public List<OrderMaster> findByCreateTimeBetween(Date startTime, Date endTime) {
        return orderMasterRepository.findByCreateTimeBetween(startTime, endTime);
    }

    @Transactional
    @Override
    public OrderMaster save(OrderMaster orderMaster) {
        return orderMasterRepository.save(orderMaster);
    }

    @Override
    public OrderMaster findByExpressNumberAndOrderStatusNotIn(String expressNumber, List<Integer> orderStatusList) {
        return orderMasterRepository.findByExpressNumberAndOrderStatusNotIn(expressNumber, orderStatusList);
    }

    @Override
    public List<OrderMaster> findByExpressNumberIn(List<String> expressNumberList) {
        return orderMasterRepository.findByExpressNumberIn(expressNumberList);
    }

    @Override
    public List<OrderMaster> findByBuyerOrderIdIn(List<String> buyerOrderIdList) {
        return orderMasterRepository.findByBuyerOrderIdIn(buyerOrderIdList);
    }

    @Override
    public OrderMaster findByExpressNumberAndOrderStatus(String expressNumber, Integer orderStatus) {
        return orderMasterRepository.findByExpressNumberAndOrderStatus(expressNumber, orderStatus);
    }

    @Override
    public List<OrderMaster> findByBuyerOpenidAndOrderStatusAndUpdateTimeIn(String buyerOpenid, Integer orderStatus, Date beginDate, Date endDate) {
        return orderMasterRepository.findByBuyerOpenidAndOrderStatusAndUpdateTimeBetween(buyerOpenid, orderStatus, beginDate, endDate);
    }

    @Override
    public List<OrderMaster> findByOrderStatus(Integer orderStatus) {
        return orderMasterRepository.findByOrderStatus(orderStatus);
    }

    @Override
    public List<OrderMaster> findByOrderStatusOrderByLastNotifyTime(Integer orderStatus) {
        return orderMasterRepository.findByOrderStatusOrderByLastNotifyTime(orderStatus);
    }

    @Override
    public List<OrderDetail> findDetailByOrderIdIn(List<String> orderIdList) {
        return orderDetailRepository.findByOrderIdIn(orderIdList);
    }

    @Override
    public Page<OrderDetail> findDetailByOrderIdInAndProductId(List<String> orderIdList, String productId, Pageable pageable) {
        return orderDetailRepository.findByOrderIdInAndProductId(orderIdList, productId, pageable);
    }

    public static void main(String[] args) {
        OrderMasterServiceImpl orderMasterService = new OrderMasterServiceImpl();
        System.out.println(orderMasterService.computeWeightRound(0.1));
        System.out.println(orderMasterService.computeWeightRound(0.2));
        System.out.println(orderMasterService.computeWeightRound(0.4));
        System.out.println(orderMasterService.computeWeightRound(0.9));
        System.out.println(orderMasterService.computeWeightRound(1.01));
        System.out.println(orderMasterService.computeWeightRound(1.1));
        System.out.println(orderMasterService.computeWeightRound(1.001));
        System.out.println(orderMasterService.computeWeightRound(1.2));
        System.out.println(orderMasterService.computeWeightRound(0.999));
    }
}
