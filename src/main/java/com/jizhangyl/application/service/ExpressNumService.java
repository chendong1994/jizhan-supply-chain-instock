package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.ExpressNum;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumService {

    void delete(Integer id);

    ExpressNum save(ExpressNum expressNum);

    List<ExpressNum> saveInBatch(List<ExpressNum> expressNumList);

    ExpressNum findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNum findOne(Integer id);

    ExpressNum findByExpNum(String expNum);

    List<ExpressNum> findAll();

    Integer countByStatus(Integer status);
}
