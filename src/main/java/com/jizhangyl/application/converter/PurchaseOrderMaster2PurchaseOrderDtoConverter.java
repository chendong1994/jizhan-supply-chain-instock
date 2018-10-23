package com.jizhangyl.application.converter;

import com.jizhangyl.application.dataobject.PurchaseOrderMaster;
import com.jizhangyl.application.dto.PurchaseOrderDto;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/21 11:10
 * @description
 */
public class PurchaseOrderMaster2PurchaseOrderDtoConverter {

    public static PurchaseOrderDto convert(PurchaseOrderMaster purchaseOrderMaster) {
        PurchaseOrderDto purchaseOrderDto = new PurchaseOrderDto();
        BeanUtils.copyProperties(purchaseOrderMaster, purchaseOrderDto);
        return purchaseOrderDto;
    }

    public static List<PurchaseOrderDto> convert(List<PurchaseOrderMaster> purchaseOrderMasterList) {
        return purchaseOrderMasterList.stream().map(e -> convert(e)).collect(Collectors.toList());
    }
}
