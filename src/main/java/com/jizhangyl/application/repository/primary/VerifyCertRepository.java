package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.VerifyCert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/28 11:42
 * @description
 */
public interface VerifyCertRepository extends JpaRepository<VerifyCert, Integer> {

    List<VerifyCert> findByOrderId(String orderId);
}
