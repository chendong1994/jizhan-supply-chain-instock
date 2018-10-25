package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:38
 * @description
 */
public interface BrandService {

    /**
     * 根据品牌 id 查询品牌名称
     * @param brandId
     * @return
     */
    Brand findOne(Integer brandId);

//    List<String> find

    Brand findByName(String name);

    Page<Brand> findAll(Pageable pageable);

    Brand save(Brand brand);

    void delete(Integer id);

    List<Brand> findByBrandName(String name);

    List<Brand> findByIdIn(List<Integer> brandIdList);
}
