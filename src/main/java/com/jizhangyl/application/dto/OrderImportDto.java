package com.jizhangyl.application.dto;

import com.jizhangyl.application.dataobject.OrderImportDetail;
import com.jizhangyl.application.dataobject.OrderImportMaster;
import lombok.Data;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/17 16:53
 * @description
 */
@Data
public class OrderImportDto extends OrderImportMaster {

    private List<OrderImportDetail> orderImportDetailList;
}
