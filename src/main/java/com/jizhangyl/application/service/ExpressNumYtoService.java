package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ExpressNumYto;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumYtoService {

    void delete(Integer id);

    ExpressNumYto save(ExpressNumYto expressNumYto);

    List<ExpressNumYto> saveInBatch(List<ExpressNumYto> expressNumYtoList);

    ExpressNumYto findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumYto findOne(Integer id);

    ExpressNumYto findByExpNum(String expNum);

    List<ExpressNumYto> findAll();

    Integer countByStatus(Integer status);
}
