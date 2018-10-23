package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.ConfirmCert;
import com.jizhangyl.application.repository.ConfirmCertRepository;
import com.jizhangyl.application.service.ConfirmCertService;
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
public class ConfirmCertServiceImpl implements ConfirmCertService {

    @Autowired
    private ConfirmCertRepository confirmCertRepository;

    @Override
    public ConfirmCert save(ConfirmCert confirmCert) {
        return confirmCertRepository.save(confirmCert);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ConfirmCert> save(List<ConfirmCert> confirmCertList) {
        return confirmCertRepository.save(confirmCertList);
    }

    @Override
    public List<ConfirmCert> findByConfirmId(String confirmId) {
        return confirmCertRepository.findByConfirmId(confirmId);
    }

    @Override
    public List<ConfirmCert> findByConfirmIdIn(List<String> confirmIdList) {
        return confirmCertRepository.findByConfirmIdIn(confirmIdList);
    }
}
