package com.jizhangyl.application.VO;

import com.jizhangyl.application.dataobject.ConfirmCert;
import com.jizhangyl.application.dataobject.PurchaseConfirmDetail;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/19 14:40
 * @description
 */
@Data
public class PurchaseConfirmDetailVO {

    private List<PurchaseConfirmDetail> purchaseConfirmDetailList;

//    private List<String> certList;
    private List<ConfirmCertVO> certList;

}
