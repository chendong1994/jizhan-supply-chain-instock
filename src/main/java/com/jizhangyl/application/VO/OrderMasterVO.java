package com.jizhangyl.application.VO;

import com.jizhangyl.application.dataobject.primary.OrderMaster;
import lombok.Data;

/**
 * @author 杨贤达
 * @date 2018/9/25 14:10
 * @description
 */
@Data
public class OrderMasterVO extends OrderMaster {

    private boolean status;

    private String buyerInviteCode;
    
    private Integer notifyCount; // 累计短信提醒次数

}
