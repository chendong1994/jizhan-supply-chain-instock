package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.OrderImportMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/17 16:54
 * @description
 */
public interface OrderImportMasterRepository extends JpaRepository<OrderImportMaster, Integer> {

    OrderImportMaster findByBuyerOpenidAndImportDate(String buyerOpenid, Date importDate);

    List<OrderImportMaster> findByBuyerOpenid(String buyerOpenid);

    List<OrderImportMaster> findByBuyerOpenidOrderByImportDateDesc(String buyerOpenid);
}
