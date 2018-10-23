package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.ConfirmCert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 11:49
 * @description
 */
public interface ConfirmCertRepository extends JpaRepository<ConfirmCert, Integer> {

    List<ConfirmCert> findByConfirmId(String confirmId);

    List<ConfirmCert> findByConfirmIdIn(List<String> confirmIdList);
}
