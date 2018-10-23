package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.PayCert;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 11:50
 * @description
 */
public interface PayCertService {

    PayCert save(PayCert payCert);

    List<PayCert> save(List<PayCert> payCertList);

    List<PayCert> findByPayId(String payId);

    List<PayCert> findByPayIdIn(List<String> payIdList);
}
