package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ConfirmCert;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/18 11:50
 * @description
 */
public interface ConfirmCertService {

    ConfirmCert save(ConfirmCert confirmCert);

    List<ConfirmCert> save(List<ConfirmCert> confirmCertList);

    List<ConfirmCert> findByConfirmId(String confirmId);

    List<ConfirmCert> findByConfirmIdIn(List<String> confirmIdList);
}
