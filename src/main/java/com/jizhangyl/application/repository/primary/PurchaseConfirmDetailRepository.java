package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.PurchaseConfirmDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/20 16:27
 * @description
 */
public interface PurchaseConfirmDetailRepository extends JpaRepository<PurchaseConfirmDetail, String> {

    List<PurchaseConfirmDetail> findByConfirmIdIn(List<String> confirmIdList);

}
