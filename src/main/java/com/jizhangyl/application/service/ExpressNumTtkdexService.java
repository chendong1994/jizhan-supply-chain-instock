package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ExpressNumTtkdex;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumTtkdexService {

    void delete(Integer id);

    ExpressNumTtkdex save(ExpressNumTtkdex expressNumTtkdex);

    List<ExpressNumTtkdex> saveInBatch(List<ExpressNumTtkdex> expressNumTtkdexList);

    ExpressNumTtkdex findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumTtkdex findOne(Integer id);

    ExpressNumTtkdex findByExpNum(String expNum);

    List<ExpressNumTtkdex> findAll();

    Integer countByStatus(Integer status);
}
