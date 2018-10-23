package com.jizhangyl.application.converter;

import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.PurchaseOrderDetail;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/23 12:39
 * @description
 */
public class PurchaseOrderDetail2OrderDetailConverter {

    public static List<OrderDetail> convert(List<PurchaseOrderDetail> purchaseOrderDetailList) {
        return purchaseOrderDetailList.stream().map(e -> convert(e)).collect(Collectors.toList());
    }

    public static OrderDetail convert(PurchaseOrderDetail purchaseOrderDetail) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProductId(String.valueOf(purchaseOrderDetail.getProductId()));
        orderDetail.setProductQuantity(purchaseOrderDetail.getProductQuantity());
        return orderDetail;
    }
}
