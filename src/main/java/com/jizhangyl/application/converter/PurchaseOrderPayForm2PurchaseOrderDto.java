package com.jizhangyl.application.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jizhangyl.application.dataobject.PurchaseOrderDetail;
import com.jizhangyl.application.dto.PurchaseOrderDto;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.PurchaseOrderPayForm;
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
public class PurchaseOrderPayForm2PurchaseOrderDto {

    public static PurchaseOrderDto convert(PurchaseOrderPayForm purchaseOrderPayForm) {
        PurchaseOrderDto purchaseOrderDto = new PurchaseOrderDto();
        purchaseOrderDto.setOrderId(purchaseOrderPayForm.getOrderId());
        purchaseOrderDto.setProviderId(purchaseOrderPayForm.getProviderId());

        List<PurchaseOrderDetail> purchaseOrderDetailList = new ArrayList<>();
        List<String> certUrlList = new ArrayList<>();

        try {
            String certUrls = purchaseOrderPayForm.getCertUrls();
            if (!StringUtils.isEmpty(certUrls) && certUrls.contains("\"")) {
                certUrls = certUrls.replace("\"", "");
            }
            String[] certUrlArray = certUrls.split(",");
            certUrlList = Arrays.asList(certUrlArray);

            Gson gson = new Gson();
            purchaseOrderDetailList = gson.fromJson(purchaseOrderPayForm.getItems(), new TypeToken<List<PurchaseOrderDetail>>() {
            }.getType());

            purchaseOrderDto.setPayAmount(purchaseOrderPayForm.getPayAmount());
            purchaseOrderDto.setComment(purchaseOrderPayForm.getComment());

        } catch (Exception e) {
            log.error("【对象转换】错误, items = {}, certUrls = {}",
                    purchaseOrderPayForm.getItems(),
                    purchaseOrderPayForm.getCertUrls());
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(),
                    String.format(ResultEnum.PARAM_ERROR.getMessage(),
                            purchaseOrderPayForm.getItems() + "," + purchaseOrderPayForm.getCertUrls()));
        }
        purchaseOrderDto.setPurchaseOrderDetailList(purchaseOrderDetailList);
        purchaseOrderDto.setCertUrlList(certUrlList);

        return purchaseOrderDto;
    }
}
