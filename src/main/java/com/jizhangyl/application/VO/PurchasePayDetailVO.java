package com.jizhangyl.application.VO;

import com.jizhangyl.application.dataobject.primary.PurchasePayDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/19 14:40
 * @description
 */
@Data
public class PurchasePayDetailVO {

    private List<PurchasePayDetail> purchasePayDetailList;

    private List<PayCertVO> certList;

    private BigDecimal payAmount;

    private String comment;

}
