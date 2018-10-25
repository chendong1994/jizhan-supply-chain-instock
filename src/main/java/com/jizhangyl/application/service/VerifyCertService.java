package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.VerifyCert;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/28 11:42
 * @description
 */
public interface VerifyCertService {

    VerifyCert save(VerifyCert verifyCert);

    List<VerifyCert> save(List<VerifyCert> verifyCertList);

    List<VerifyCert> findByOrderId(String orderId);
}
