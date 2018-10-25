package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ExpressNumSto;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumStoService {

    void delete(Integer id);

    ExpressNumSto save(ExpressNumSto expressNumSto);

    List<ExpressNumSto> saveInBatch(List<ExpressNumSto> expressNumStoList);

    ExpressNumSto findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumSto findOne(Integer id);

    ExpressNumSto findByExpNum(String expNum);

    List<ExpressNumSto> findAll();

    Integer countByStatus(Integer status);
}
