package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.OrderImportDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/17 16:55
 * @description
 */
public interface OrderImportDetailRepository extends JpaRepository<OrderImportDetail, Integer> {

    List<OrderImportDetail> findByMasterId(String masterId);
}
