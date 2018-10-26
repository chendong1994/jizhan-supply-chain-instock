package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.VO.CateVO;
import com.jizhangyl.application.dataobject.primary.Cate;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.CateForm;
import com.jizhangyl.application.repository.primary.CateRepository;
import com.jizhangyl.application.service.CateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:39
 * @description
 */
@Service
public class CateServiceImpl implements CateService {

    @Autowired
    private CateRepository cateRepository;

    @Override
    public Cate findOne(Integer cateId) {
        return cateRepository.getOne(cateId);
    }

    @Override
    public List<CateVO> findAll() {
        List<Cate> cateList = cateRepository.findAll();
        List<CateVO> cateVOList = cateList.stream()
                .map(e -> new CateVO(e.getId(), e.getName()))
                .collect(Collectors.toList());
        return cateVOList;
    }

    @Override
    public void delete(Integer cateId) {
        if (cateId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
//        Cate cate = cateRepository.findOne(cateId);
        Cate cate = cateRepository.getOne(cateId);
        if (cate == null) {
            throw new GlobalException(ResultEnum.CATE_NOT_EXIST);
        }
//        cateRepository.delete(cateId);
        cateRepository.deleteById(cateId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> cateIdList) {
        if (CollectionUtils.isEmpty(cateIdList)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<Cate> cateList = new ArrayList<>();
        for (Integer cateId : cateIdList) {
//            Cate cate = cateRepository.findOne(cateId);
            Cate cate = cateRepository.getOne(cateId);
            if (cate == null) {
                throw new GlobalException(ResultEnum.CATE_NOT_EXIST.getCode(), "类目编号：" + cateId);
            }
            cateList.add(cate);
        }
//        cateRepository.delete(cateList);
        cateRepository.deleteAll(cateList);
    }

    @Override
    public void update(CateForm cateForm) {
        if (cateForm == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
//        Cate cate = cateRepository.findOne(cateForm.getId());
        Cate cate = cateRepository.getOne(cateForm.getId());
        if (cate == null) {
            throw new GlobalException(ResultEnum.CATE_NOT_EXIST);
        }
        if (findByName(cateForm.getName()) != null) {
            throw new GlobalException(ResultEnum.CATE_IS_EXIST.getCode(), String.format(ResultEnum.CATE_IS_EXIST.getMessage(), cateForm.getName()));
        }
        cate.setName(cateForm.getName());
        cateRepository.save(cate);
    }

    @Override
    public void save(CateForm cateForm) {
        if (cateForm == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        if (findByName(cateForm.getName()) != null) {
            throw new GlobalException(ResultEnum.CATE_IS_EXIST.getCode(), String.format(ResultEnum.CATE_IS_EXIST.getMessage(), cateForm.getName()));
        }
        Cate cate = new Cate();
        cate.setName(cateForm.getName());
        cateRepository.save(cate);
    }

    @Override
    public Cate findByName(String name) {
        return cateRepository.findByName(name);
    }

    @Override
    public List<Cate> findByIdIn(List<Integer> cateIdList) {
        return cateRepository.findByIdIn(cateIdList);
    }

    @Override
    public List<Cate> findByNameLike(String name) {
        return cateRepository.findByNameLike("%" + name + "%");
    }
}
