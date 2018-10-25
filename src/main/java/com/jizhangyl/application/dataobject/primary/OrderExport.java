package com.jizhangyl.application.dataobject.primary;

import com.jizhangyl.application.VO.ShopExportVo;
import lombok.Data;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/22 20:33
 * @description
 */
@Data
public class OrderExport {

    private String tbOrderId;

    private String buyerName;

    private String recvName;

    private String recvAddr;

    private String recvMobile;

    private List<ShopExportVo> shopExportVoList;

    private String expressNumber;

    private String jizhanOrderId;

    private String orderStatus;

    private String orderTime;
}
