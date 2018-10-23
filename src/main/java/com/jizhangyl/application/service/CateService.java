package com.jizhangyl.application.service;

import com.jizhangyl.application.VO.CateVO;
import com.jizhangyl.application.dataobject.Cate;
import com.jizhangyl.application.form.CateForm;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:38
 * @description
 */
public interface CateService {
    Cate findOne(Integer cateId);

    List<CateVO> findAll();

    void delete(Integer cateId);

    void delete(List<Integer> cateIdList);

    void update(CateForm cateForm);

    void save(CateForm cateForm);

    Cate findByName(String name);

    List<Cate> findByIdIn(List<Integer> cateIdList);

    List<Cate> findByNameLike(String name);
}
