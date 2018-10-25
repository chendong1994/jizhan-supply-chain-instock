package com.jizhangyl.application.VO;

import com.jizhangyl.application.dataobject.primary.PurchaseOrderConfirm;
import lombok.Data;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/19 10:08
 * @description
 */
@Data
public class PurchaseOrderConfirmVO extends PurchaseOrderConfirm {

    private List<String> certUrlList;

}
