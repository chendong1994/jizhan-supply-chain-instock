package com.jizhangyl.application.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderDetail;
import com.jizhangyl.application.dto.PurchaseOrderDto;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.PurchaseOrderConfirmForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/22 14:34
 * @description
 */
@Slf4j
public class PurchaseOrderConfirmForm2PurchaseOrderDto {

    public static PurchaseOrderDto convert(PurchaseOrderConfirmForm purchaseOrderConfirmForm) {
        PurchaseOrderDto purchaseOrderDto = new PurchaseOrderDto();
        purchaseOrderDto.setOrderId(purchaseOrderConfirmForm.getOrderId());
        purchaseOrderDto.setProviderId(purchaseOrderConfirmForm.getProviderId());

        List<PurchaseOrderDetail> purchaseOrderDetailList = new ArrayList<>();
        List<String> certUrlList = new ArrayList<>();

        try {
            String certUrls = purchaseOrderConfirmForm.getCertUrls();
            if (!StringUtils.isEmpty(certUrls) && certUrls.contains("\"")) {
                certUrls = certUrls.replace("\"", "");
            }
            String[] certUrlArray = certUrls.split(",");
            certUrlList = Arrays.asList(certUrlArray);

            Gson gson = new Gson();
            purchaseOrderDetailList = gson.fromJson(purchaseOrderConfirmForm.getItems(), new TypeToken<List<PurchaseOrderDetail>>() {
            }.getType());
        } catch (Exception e) {
            log.error("【对象转换】错误, items = {}, certUrls = {}",
                    purchaseOrderConfirmForm.getItems(),
                    purchaseOrderConfirmForm.getCertUrls());
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(),
                    String.format(ResultEnum.PARAM_ERROR.getMessage(),
                            purchaseOrderConfirmForm.getItems() + "," + purchaseOrderConfirmForm.getCertUrls()));
        }
        purchaseOrderDto.setPurchaseOrderDetailList(purchaseOrderDetailList);
        purchaseOrderDto.setCertUrlList(certUrlList);

        return purchaseOrderDto;
    }
}
