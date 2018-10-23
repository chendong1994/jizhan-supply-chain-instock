package com.jizhangyl.application.dto;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年9月25日 下午7:41:30
 * @description 订单管理页面->订单详情窗口->收件人信息
 */
@Data
public class QueryOrderDetailReceiverDTO {

    private String recipient; // 收件人
    
    private String recipientPhone; // 收件人电话
    
    private String recipientAddr; // 收件人地址
}
