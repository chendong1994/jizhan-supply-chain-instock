package com.jizhangyl.application.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderDetail;
import com.jizhangyl.application.dto.PurchaseOrderDto;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.PurchaseOrderForm;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/20 18:01
 * @description
 */
@Slf4j
public class PurchaseOrderForm2PurchaseOrderDto {

    public static PurchaseOrderDto convert(PurchaseOrderForm purchaseOrderForm) {
        PurchaseOrderDto purchaseOrderDto = new PurchaseOrderDto();
        purchaseOrderDto.setProviderId(purchaseOrderForm.getProviderId());
        purchaseOrderDto.setLoanDate(purchaseOrderForm.getLoanDate());

        List<PurchaseOrderDetail> purchaseOrderDetailList = new ArrayList<>();

        try {
            Gson gson = new Gson();
            purchaseOrderDetailList = gson.fromJson(purchaseOrderForm.getItems(), new TypeToken<List<PurchaseOrderDetail>>() {
            }.getType());
        } catch (Exception e) {
            log.error("【对象转换】错误, string = {}", purchaseOrderForm.getItems());
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(), purchaseOrderForm.getItems()));
        }
        purchaseOrderDto.setPurchaseOrderDetailList(purchaseOrderDetailList);

        return purchaseOrderDto;
    }
}
