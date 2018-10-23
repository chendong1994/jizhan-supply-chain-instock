package com.jizhangyl.application.converter;

import com.jizhangyl.application.VO.ShopExportVo;
import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.OrderExport;
import com.jizhangyl.application.dataobject.OrderMaster;
import com.jizhangyl.application.dataobject.Shop;
import com.jizhangyl.application.dataobject.WxuserAddr;
import com.jizhangyl.application.service.OrderDetailService;
import com.jizhangyl.application.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/22 20:32
 * @description
 */
@Slf4j
@Service
public class OrderMaster2OrderExportConverter {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShopService shopService;

    public OrderExport convert(OrderMaster orderMaster, List<WxuserAddr> wxuserAddrList, List<Shop> shopList,
                               List<OrderDetail> detailList) {

        OrderExport orderExport = new OrderExport();
        orderExport.setTbOrderId(orderMaster.getBuyerOrderId());
        orderExport.setBuyerName(orderMaster.getCustomerName());
        orderExport.setRecvName(orderMaster.getRecipient());

        for (WxuserAddr addr : wxuserAddrList) {
            if (addr.getId().equals(orderMaster.getRecipientAddrId())) {
                StringBuffer sb = new StringBuffer();
                sb.append(addr.getProvince()).append(" ")
                        .append(addr.getCity()).append(" ")
                        .append(addr.getArea()).append(" ")
                        .append(addr.getDetailAddr());
                orderExport.setRecvAddr(sb.toString());
                orderExport.setRecvMobile(addr.getPhone());
            }
        }

        // 每个订单的 orderDetailList
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (OrderDetail orderDetail : detailList) {
            if (orderDetail.getOrderId().equals(orderMaster.getOrderId())) {
                orderDetailList.add(orderDetail);
            }
        }

        List<ShopExportVo> shopExportVoList = new ArrayList<>();
        for (OrderDetail detail : orderDetailList) {
            Shop shop = null;
            for (Shop s : shopList) {
                if (s.getId().equals(Integer.parseInt(detail.getProductId()))) {
                    shop = s;
                    break;
                }
            }
            ShopExportVo shopExportVo = new ShopExportVo();
            shopExportVo.setJancode(shop.getShopJan());
            shopExportVo.setQuantity(detail.getProductQuantity());

            shopExportVoList.add(shopExportVo);
        }
        orderExport.setShopExportVoList(shopExportVoList);

        orderExport.setExpressNumber(orderMaster.getExpressNumber());
        orderExport.setJizhanOrderId(orderMaster.getOrderId());
        orderExport.setOrderStatus(orderMaster.getOrderStatusEnum().getMsg());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderExport.setOrderTime(sdf.format(orderMaster.getCreateTime()));

        return orderExport;
    }

    public List<OrderExport> convert(List<OrderMaster> orderMasterList, List<WxuserAddr> wxuserAddrList) {

        List<String> orderIdList = orderMasterList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());

        List<OrderDetail> detailList = orderDetailService.findByOrderIdIn(orderIdList);

        List<Integer> shopIdList = detailList.stream().map(e -> Integer.parseInt(e.getProductId())).collect(Collectors.toList());

        List<Shop> shopList = shopService.findByIdIn(shopIdList);

        return orderMasterList.stream().map(e -> convert(e, wxuserAddrList, shopList, detailList)).collect(Collectors.toList());
    }
}
