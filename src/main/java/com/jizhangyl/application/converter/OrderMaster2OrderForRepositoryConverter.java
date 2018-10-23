package com.jizhangyl.application.converter;

import com.jizhangyl.application.dataobject.BuyerInfo;
import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.OrderForRepository;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dataobject.Shop;
import com.jizhangyl.application.dataobject.Wxuser;
import com.jizhangyl.application.dataobject.WxuserAddr;
import com.jizhangyl.application.dataobject.WxuserSender;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.service.OrderDetailService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.utils.RoundUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/22 20:32
 * @description orderMaster 转换至 orderForCustoms
 */
@Slf4j
@Service
public class OrderMaster2OrderForRepositoryConverter {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShopService shopService;

    public List<OrderForRepository> convert(List<OrderMaster> orderMasterList, List<WxuserAddr> wxuserAddrList, BuyerInfo buyerInfo) {

        List<OrderForRepository> orderForRepositoryList = new ArrayList<>();

        List<Wxuser> wxuserList = buyerInfo.getWxuserList();

        List<WxuserSender> wxuserSenderList = buyerInfo.getWxuserSenderList();

        List<String> orderIdList = orderMasterList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());

        List<OrderDetail> detailList = orderDetailService.findByOrderIdIn(orderIdList);

        List<Integer> shopIdList = detailList.stream().map(e -> Integer.parseInt(e.getProductId())).collect(Collectors.toList());

        List<Shop> shopList = shopService.findByIdIn(shopIdList);

        for (OrderMaster orderMaster : orderMasterList) {

            if (orderMaster.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {

                OrderForRepository orderForRepository = new OrderForRepository();

                orderForRepository.setRecvName(orderMaster.getRecipient());
                orderForRepository.setBuyerIdNum(orderMaster.getRecipientIdCard());

                for (WxuserAddr addr : wxuserAddrList) {
                    if (addr.getId().equals(orderMaster.getRecipientAddrId())) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(addr.getProvince()).append(" ")
                                .append(addr.getCity()).append(" ")
                                .append(addr.getArea()).append(" ")
                                .append(addr.getDetailAddr());
                        orderForRepository.setRecvMobile(addr.getPhone());
                        orderForRepository.setRecvAddr(sb.toString());
                        orderForRepository.setProvince(addr.getProvince());
                        orderForRepository.setCity(addr.getCity());
                        orderForRepository.setPostCode(addr.getPostCode());
                        break;
                    }
                }

                for (Wxuser wxuser : wxuserList) {
                    if (wxuser.getOpenId().equals(orderMaster.getBuyerOpenid())) {
                        orderForRepository.setInviteCode(wxuser.getInviteCode());
                        break;
                    }
                }

                for (WxuserSender wxuserSender : wxuserSenderList) {
                    if (wxuserSender.getId().equals(orderMaster.getBuyerAddrId())) {
                        orderForRepository.setBuyerName(wxuserSender.getSender());
                        orderForRepository.setBuyerMobile(wxuserSender.getPhone());
                        break;
                    }
                }

                orderForRepository.setExpressNumber(orderMaster.getExpressNumber());


                String productJancode = "";
                String productSimpleCode = "";
                BigDecimal totalBcPrice = BigDecimal.ZERO;
                BigDecimal totalTaxes = BigDecimal.ZERO;
                BigDecimal totalPackWeight = BigDecimal.ZERO;
                BigDecimal totalGPrice = BigDecimal.ZERO;
                BigDecimal totalDuties = BigDecimal.ZERO;
                BigDecimal actualPackWeight = BigDecimal.ZERO;
                BigDecimal actualFreight = BigDecimal.ZERO;

                // 每个订单的 orderDetailList
                List<OrderDetail> orderDetailList = new ArrayList<>();
                for (OrderDetail orderDetail : detailList) {
                    if (orderDetail.getOrderId().equals(orderMaster.getOrderId())) {
                        orderDetailList.add(orderDetail);
                    }
                }

                // 包裹毛重
                Integer netWeight  = 100;
                Integer jWeight = 0;

                for (OrderDetail detail : orderDetailList) {

                    Shop shop = null;
                    for (Shop s : shopList) {
                        if (s.getId().equals(Integer.parseInt(detail.getProductId()))) {
                            shop = s;
                            break;
                        }
                    }

                    String jancode = shop.getShopJan();
                    if (!StringUtils.isEmpty(jancode) && !productJancode.contains(jancode)) {
                        productJancode += jancode + "/";
                    }

                    // DONE -> 新增商品的 simpleCode 后修改此部分
                    String simpleCode = shop.getPackCode();
                    if (!StringUtils.isEmpty(simpleCode)) {
                        productSimpleCode += simpleCode + "*" + detail.getProductQuantity() + "/";
                    }

                    /**
                     * 该件商品数量
                     */
                    BigDecimal quantity = new BigDecimal(detail.getProductQuantity());

                    // 计算打包重量
                    totalPackWeight = totalPackWeight.add(new BigDecimal(shop.getShopDweight()).multiply(quantity));

                    // 计算供货价
                    totalGPrice = totalGPrice.add(shop.getShopGprice().multiply(quantity));


                    // 计算毛重
                    netWeight += shop.getShopJweight() * detail.getProductQuantity();

                }

                // 计算实收包裹重量
                actualPackWeight = new BigDecimal(RoundUtil.computeRound(totalPackWeight.divide(new BigDecimal(1000)).doubleValue()));

                // 计算订单实收运费
                actualFreight = orderMaster.getOrderFreight();

                if (!StringUtils.isEmpty(productJancode)) {
                    orderForRepository.setProductJancode(productJancode.substring(0, productJancode.length() - 1));
                }

                orderForRepository.setOrderId(orderMaster.getOrderId());

                if (!StringUtils.isEmpty(productSimpleCode)) {
                    orderForRepository.setProductSimpleCode(productSimpleCode.substring(0, productSimpleCode.length() - 1));
                }

                // 毛重
                orderForRepository.setNetWeight(String.format("%.2f", (double)netWeight / 1000));
                orderForRepository.setJWeight(String.format("%.2f", (double) (netWeight - 100) / 1000));
                orderForRepository.setTotalBcPrice(String.format("%.2f", totalBcPrice));
                orderForRepository.setTotalTaxes(String.format("%.2f", totalTaxes));
                orderForRepository.setTotalPackWeight(String.format("%.2f", totalPackWeight.divide(new BigDecimal(1000))));
                orderForRepository.setTotalGPrice(String.format("%.2f", totalGPrice));
                orderForRepository.setTotalDuties(String.format("%.2f", totalDuties));
                orderForRepository.setActualPackWeight(String.format("%.2f", actualPackWeight));
                orderForRepository.setActualFreight(String.format("%.2f", actualFreight));
                orderForRepository.setOrderStatus(orderMaster.getOrderStatusEnum().getMsg());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                orderForRepository.setOrderTime(sdf.format(orderMaster.getCreateTime()));

                orderForRepositoryList.add(orderForRepository);
            }
        }
        return orderForRepositoryList;
    }
    
    /**
     * 转换全部的订单(各种状态的都转换)
     * @param orderMasterList
     * @param wxuserAddrList
     * @param buyerInfo
     * @return
     */
    public List<OrderForRepository> convertAll(List<OrderMaster> orderMasterList, List<WxuserAddr> wxuserAddrList, BuyerInfo buyerInfo) {
        
        List<OrderForRepository> orderForRepositoryList = new ArrayList<>();
        
        List<Wxuser> wxuserList = buyerInfo.getWxuserList();
        
        List<WxuserSender> wxuserSenderList = buyerInfo.getWxuserSenderList();
        
        for (OrderMaster orderMaster : orderMasterList) {
            
            OrderForRepository orderForRepository = new OrderForRepository();
            
            orderForRepository.setRecvName(orderMaster.getRecipient());
            orderForRepository.setBuyerIdNum(orderMaster.getRecipientIdCard());
            
            for (WxuserAddr addr : wxuserAddrList) {
                if (addr.getId().equals(orderMaster.getRecipientAddrId())) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(addr.getProvince()).append(" ")
                    .append(addr.getCity()).append(" ")
                    .append(addr.getArea()).append(" ")
                    .append(addr.getDetailAddr());
                    orderForRepository.setRecvMobile(addr.getPhone());
                    orderForRepository.setRecvAddr(sb.toString());
                    orderForRepository.setProvince(addr.getProvince());
                    orderForRepository.setCity(addr.getCity());
                    orderForRepository.setPostCode(addr.getPostCode());
                    break;
                }
            }
            
            for (Wxuser wxuser : wxuserList) {
                if (wxuser.getOpenId().equals(orderMaster.getBuyerOpenid())) {
                    orderForRepository.setInviteCode(wxuser.getInviteCode());
                    break;
                }
            }
            
            for (WxuserSender wxuserSender : wxuserSenderList) {
                if (wxuserSender.getId().equals(orderMaster.getBuyerAddrId())) {
                    orderForRepository.setBuyerName(wxuserSender.getSender());
                    orderForRepository.setBuyerMobile(wxuserSender.getPhone());
                    break;
                }
            }
            
            orderForRepository.setExpressNumber(orderMaster.getExpressNumber());
            
            String productJancode = "";
            String productSimpleCode = "";
            BigDecimal totalBcPrice = BigDecimal.ZERO;
            BigDecimal totalTaxes = BigDecimal.ZERO;
            BigDecimal totalPackWeight = BigDecimal.ZERO;
            BigDecimal totalGPrice = BigDecimal.ZERO;
            BigDecimal totalDuties = BigDecimal.ZERO;
            BigDecimal actualPackWeight = BigDecimal.ZERO;
            BigDecimal actualFreight = BigDecimal.ZERO;
            
            
            List<OrderDetail> orderDetailList = orderDetailService.findByOrderId(orderMaster.getOrderId());
            
            // 包裹毛重
            Integer netWeight  = 100;
            Integer jWeight = 0;
            
            for (OrderDetail detail : orderDetailList) {
                
                Shop shop = shopService.findOne(Integer.parseInt(detail.getProductId()));
                
                String jancode = shop.getShopJan();
                if (!StringUtils.isEmpty(jancode) && !productJancode.contains(jancode)) {
                    productJancode += jancode + "/";
                }
                
                // DONE -> 新增商品的 simpleCode 后修改此部分
                String simpleCode = shop.getPackCode();
                if (!StringUtils.isEmpty(simpleCode)) {
                    productSimpleCode += simpleCode + "*" + detail.getProductQuantity() + "/";
                }
                
                /**
                 * 该件商品数量
                 */
                BigDecimal quantity = new BigDecimal(detail.getProductQuantity());
                
                // 计算打包重量
                totalPackWeight = totalPackWeight.add(new BigDecimal(shop.getShopDweight()).multiply(quantity));
                
                // 计算供货价
                totalGPrice = totalGPrice.add(shop.getShopGprice().multiply(quantity));
                
                // 计算毛重
                netWeight += shop.getShopJweight() * detail.getProductQuantity();
                
            }
            
            // 计算实收包裹重量
            actualPackWeight = new BigDecimal(RoundUtil.computeRound(totalPackWeight.divide(new BigDecimal(1000)).doubleValue()));
            
            // 计算订单实收运费
            actualFreight = orderMaster.getOrderFreight();
            
            if (!StringUtils.isEmpty(productJancode)) {
                orderForRepository.setProductJancode(productJancode.substring(0, productJancode.length() - 1));
            }
            
            orderForRepository.setOrderId(orderMaster.getOrderId());
            
            if (!StringUtils.isEmpty(productSimpleCode)) {
                orderForRepository.setProductSimpleCode(productSimpleCode.substring(0, productSimpleCode.length() - 1));
            }
            
            // 毛重
            orderForRepository.setNetWeight(String.format("%.2f", (double)netWeight / 1000));
            orderForRepository.setJWeight(String.format("%.2f", (double) (netWeight - 100) / 1000));
            orderForRepository.setTotalBcPrice(String.format("%.2f", totalBcPrice));
            orderForRepository.setTotalTaxes(String.format("%.2f", totalTaxes));
            orderForRepository.setTotalPackWeight(String.format("%.2f", totalPackWeight.divide(new BigDecimal(1000))));
            orderForRepository.setTotalGPrice(String.format("%.2f", totalGPrice));
            orderForRepository.setTotalDuties(String.format("%.2f", totalDuties));
            orderForRepository.setActualPackWeight(String.format("%.2f", actualPackWeight));
            orderForRepository.setActualFreight(String.format("%.2f", actualFreight));
            orderForRepository.setOrderStatus(orderMaster.getOrderStatusEnum().getMsg());
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            orderForRepository.setOrderTime(sdf.format(orderMaster.getCreateTime()));
            
            orderForRepositoryList.add(orderForRepository);
        }
        return orderForRepositoryList;
    }
}