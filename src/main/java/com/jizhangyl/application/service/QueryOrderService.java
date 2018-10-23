package com.jizhangyl.application.service;

import java.math.BigDecimal;
import java.util.List;

import com.jizhangyl.application.dto.QueryOrderDetailReceiverDTO;
import com.jizhangyl.application.dto.QueryOrderDetailShopDTO;
import com.jizhangyl.application.dto.QueryOrderMasterDTO;
import com.jizhangyl.application.dto.SenderDTO;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年9月25日 下午7:56:32
 * @description 处理订单管理模块的服务层接口
 */
public interface QueryOrderService {

    /**
     * 根据"物流单号,航班提单号,买手邀请码"以及"订单状态"查询订单列表
     * @param content 物流单号,航班提单号,买手邀请码
     * @param status 订单状态
     * @return 满足条件的订单列表
     */
    List<QueryOrderMasterDTO> findByExpressDeliveryInviteCodeStatus(String content, String status, String startTimeStr, String endTimeStr, Integer page, Integer size);
    
    /**
     * 根据物流单号查询该订单的物流状态信息
     * @param expNum 物流单号
     * @return 物流状态信息-列表
     */
    List<String> findLogisticsStatus(String expNum);
    
    /**
     * 根据订单id查询该订单内的所有商品详情
     * @param orderId 订单id
     * @return 该订单包含的商品详情列表(Jan code,商品标题,售价,关税,数量)
     */
    List<QueryOrderDetailShopDTO> findOrderDetailByOrderId(String orderId);
    
    /**
     * 根据订单id查询收件人信息
     * @param orderId 订单id
     * @return 该订单的收件人信息(收件人名称,收件人电话,收件人详细地址)
     */
    List<QueryOrderDetailReceiverDTO> findOrderReceiverByOrderId(String orderId);
    
    /**
     * 根据orderId查出发件人信息
     * @param orderId 订单id
     * @return 该订单的发件人信息
     */
    List<SenderDTO> findOrderSenderByOrderId(String orderId);
}
