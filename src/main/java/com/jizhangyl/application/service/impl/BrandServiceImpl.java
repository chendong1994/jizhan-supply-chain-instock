package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.Brand;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.BrandRepository;
import com.jizhangyl.application.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:39
 * @description
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Brand findOne(Integer brandId) {
        return brandRepository.getOne(brandId);
    }

    @Override
    public Brand findByName(String name) {
        return brandRepository.findByName(name);
    }

    @Override
    public Page<Brand> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Brand save(Brand brand) {

        Brand b = findByName(brand.getName());
        if (b != null) {
            throw new GlobalException(ResultEnum.BRAND_IS_EXIST.getCode(), String.format(ResultEnum.BRAND_IS_EXIST.getMessage(), brand.getName()));
        }

        Brand result = brandRepository.save(brand);
        if (result == null) {
            throw new GlobalException(ResultEnum.BRAND_ADD_FAIL);
        }
        return result;
    }

    @Override
    public void delete(Integer id) {
//        Brand brand = brandRepository.findOne(id);
        Brand brand = brandRepository.getOne(id);
        if (brand == null) {
            throw new GlobalException(ResultEnum.BRAND_NOT_EXIST);
        }
//        brandRepository.delete(id);
        brandRepository.deleteById(id);
    }

    @Override
    public List<Brand> findByBrandName(String name) {
        return brandRepository.findByNameLike("%" + name + "%");
    }

    @Override
    public List<Brand> findByIdIn(List<Integer> brandIdList) {
        return brandRepository.findByIdIn(brandIdList);
    }
}
