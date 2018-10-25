package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.PurchasePayDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/20 16:27
 * @description
 */
public interface PurchasePayDetailRepository extends JpaRepository<PurchasePayDetail, String> {

    List<PurchasePayDetail> findByPayIdIn(List<String> payIdList);

}
