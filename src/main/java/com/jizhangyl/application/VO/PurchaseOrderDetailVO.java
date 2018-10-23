package com.jizhangyl.application.VO;

import com.jizhangyl.application.dataobject.PurchaseOrderDetail;
import lombok.Data;

/**
 * @author 杨贤达
 * @date 2018/10/16 11:39
 * @description
 */
@Data
public class PurchaseOrderDetailVO extends PurchaseOrderDetail {
    private Integer boxNum;
}
