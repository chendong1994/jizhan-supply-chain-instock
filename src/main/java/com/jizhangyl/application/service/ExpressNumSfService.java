package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ExpressNumSf;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumSfService {

    void delete(Integer id);

    ExpressNumSf save(ExpressNumSf expressNumSf);

    List<ExpressNumSf> saveInBatch(List<ExpressNumSf> expressNumSfList);

    ExpressNumSf findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumSf findOne(Integer id);

    ExpressNumSf findByExpNum(String expNum);

    List<ExpressNumSf> findAll();

    Integer countByStatus(Integer status);
}
