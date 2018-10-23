package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.BuyerInfo;
import com.jizhangyl.application.dataobject.OrderMaster;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/7 16:49
 * @description
 */
public interface BuyerService {
    BuyerInfo findBuyerInfo(List<OrderMaster> orderMasterList);
}
