package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ExpressNumYundaex;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumYundaexService {

    void delete(Integer id);

    ExpressNumYundaex save(ExpressNumYundaex expressNumYundaex);

    List<ExpressNumYundaex> saveInBatch(List<ExpressNumYundaex> expressNumYundaexList);

    ExpressNumYundaex findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumYundaex findOne(Integer id);

    ExpressNumYundaex findByExpNum(String expNum);

    List<ExpressNumYundaex> findAll();

    Integer countByStatus(Integer status);
}
