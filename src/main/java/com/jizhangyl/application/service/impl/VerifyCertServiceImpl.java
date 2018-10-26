package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.VerifyCert;
import com.jizhangyl.application.repository.primary.VerifyCertRepository;
import com.jizhangyl.application.service.VerifyCertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/28 11:43
 * @description
 */
@Service
public class VerifyCertServiceImpl implements VerifyCertService {

    @Autowired
    private VerifyCertRepository verifyCertRepository;

    @Override
    public VerifyCert save(VerifyCert verifyCert) {
        return verifyCertRepository.save(verifyCert);
    }

    @Override
    public List<VerifyCert> findByOrderId(String orderId) {
        return verifyCertRepository.findByOrderId(orderId);
    }

    @Override
    public List<VerifyCert> save(List<VerifyCert> verifyCertList) {
//        return verifyCertRepository.save(verifyCertList);
        return verifyCertRepository.saveAll(verifyCertList);
    }
}
