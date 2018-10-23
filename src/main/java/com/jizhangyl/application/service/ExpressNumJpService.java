package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.ExpressNumJp;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumJpService {

    void delete(Integer id);

    ExpressNumJp save(ExpressNumJp expressNumJp);

    List<ExpressNumJp> saveInBatch(List<ExpressNumJp> expressNumJpList);

    ExpressNumJp findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumJp findOne(Integer id);

    ExpressNumJp findByExpNum(String expNum);

    List<ExpressNumJp> findAll();

    Integer countByStatus(Integer status);
}
