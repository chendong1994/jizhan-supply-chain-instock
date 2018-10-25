package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.PayCert;
import com.jizhangyl.application.repository.primary.PayCertRepository;
import com.jizhangyl.application.service.PayCertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 11:50
 * @description
 */
@Service
public class PayCertServiceImpl implements PayCertService {

    @Autowired
    private PayCertRepository payCertRepository;

    @Override
    public PayCert save(PayCert payCert) {
        return payCertRepository.save(payCert);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<PayCert> save(List<PayCert> payCertList) {
        return payCertRepository.save(payCertList);
    }

    @Override
    public List<PayCert> findByPayId(String payId) {
        return payCertRepository.findByPayId(payId);
    }

    @Override
    public List<PayCert> findByPayIdIn(List<String> payIdList) {
        return payCertRepository.findByPayIdIn(payIdList);
    }
}
