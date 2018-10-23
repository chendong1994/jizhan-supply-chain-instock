package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.ExpressNumCc;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumCcService {

    void delete(Integer id);

    ExpressNumCc save(ExpressNumCc expressNumCc);

    List<ExpressNumCc> saveInBatch(List<ExpressNumCc> expressNumCcList);

    ExpressNumCc findUnused();

    void updateStatus(Integer id, Integer code);

    ExpressNumCc findOne(Integer id);

    ExpressNumCc findByExpNum(String expNum);

    List<ExpressNumCc> findAll();

    Integer countByStatus(Integer status);
}
