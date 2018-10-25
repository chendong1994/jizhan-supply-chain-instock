package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderCurrency;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.PurchaseOrderCurrencyRepository;
import com.jizhangyl.application.service.PurchaseOrderCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨贤达
 * @date 2018/10/12 20:01
 * @description
 */
@Service
public class PurchaseOrderCurrencyServiceImpl implements PurchaseOrderCurrencyService {

    @Autowired
    private PurchaseOrderCurrencyRepository purchaseOrderCurrencyRepository;

    public PurchaseOrderCurrency save(PurchaseOrderCurrency purchaseOrderCurrency) {
        PurchaseOrderCurrency result = purchaseOrderCurrencyRepository.save(purchaseOrderCurrency);
        if (result == null) {
            throw new GlobalException(ResultEnum.ORDER_CURRENCY_SAVE_ERROR);
        }
        return result;
    }
}
