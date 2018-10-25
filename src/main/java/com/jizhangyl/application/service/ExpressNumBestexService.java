package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ExpressNumBestex;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumBestexService {

    void delete(Integer id);

    ExpressNumBestex save(ExpressNumBestex expressNumBestex);

    List<ExpressNumBestex> saveInBatch(List<ExpressNumBestex> expressNumBestexList);

    ExpressNumBestex findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumBestex findOne(Integer id);

    ExpressNumBestex findByExpNum(String expNum);

    List<ExpressNumBestex> findAll();

    Integer countByStatus(Integer status);
}
