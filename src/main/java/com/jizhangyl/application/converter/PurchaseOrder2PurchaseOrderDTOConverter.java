package com.jizhangyl.application.converter;

import com.jizhangyl.application.deprecated.PurchaseOrder2;
import com.jizhangyl.application.dto.PurchaseOrderDto;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/1 12:40
 * @description
 */
public class PurchaseOrder2PurchaseOrderDTOConverter {

    public static PurchaseOrderDto convert(PurchaseOrder2 purchaseOrder) {
        PurchaseOrderDto purchaseOrderDTO = new PurchaseOrderDto();
        BeanUtils.copyProperties(purchaseOrder, purchaseOrderDTO);
        return purchaseOrderDTO;
    }

    public static List<PurchaseOrderDto> convert(List<PurchaseOrder2> purchaseOrderList) {
        List<PurchaseOrderDto> purchaseOrderDtoList = purchaseOrderList.stream().map(e -> convert(e)).collect(Collectors.toList());
        return purchaseOrderDtoList;
    }
}
