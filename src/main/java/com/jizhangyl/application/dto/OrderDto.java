package com.jizhangyl.application.dto;

import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import lombok.Data;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/10 14:36
 * @description
 */
@Data
public class OrderDto extends OrderMaster {

    /**
     * 订单详情 list
     */
    private List<OrderDetail> orderDetailList;
}
