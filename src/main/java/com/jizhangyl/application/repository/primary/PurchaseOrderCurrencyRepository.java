package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/12 20:00
 * @description
 */
public interface PurchaseOrderCurrencyRepository extends JpaRepository<PurchaseOrderCurrency, Integer> {
}
