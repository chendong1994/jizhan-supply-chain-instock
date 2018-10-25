package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.PayCert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 11:49
 * @description
 */
public interface PayCertRepository extends JpaRepository<PayCert, Integer> {

    List<PayCert> findByPayId(String payId);

    List<PayCert> findByPayIdIn(List<String> payIdList);
}
