package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ExpressNum;
import com.jizhangyl.application.dataobject.primary.RepositoryDelivery;

import java.util.Date;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/5 11:01
 * @description
 */
public interface RepositoryDeliveryService {

    void delivery(List<ExpressNum> expressNumList);

    List<RepositoryDelivery> save(List<RepositoryDelivery> repositoryDeliveryList);

    List<RepositoryDelivery> findByCreateTimeBetween(Date beginTime, Date endTime);
}
