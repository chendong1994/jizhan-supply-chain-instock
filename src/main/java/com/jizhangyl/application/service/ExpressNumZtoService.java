package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.ExpressNumZto;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumZtoService {

    void delete(Integer id);

    ExpressNumZto save(ExpressNumZto expressNumZto);

    List<ExpressNumZto> saveInBatch(List<ExpressNumZto> expressNumZtoList);

    ExpressNumZto findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumZto findOne(Integer id);

    ExpressNumZto findByExpNum(String expNum);

    List<ExpressNumZto> findAll();

    Integer countByStatus(Integer status);
}
