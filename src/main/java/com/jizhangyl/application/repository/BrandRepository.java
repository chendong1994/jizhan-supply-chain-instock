package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.Brand;
import com.jizhangyl.application.dto.BrandDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:38
 * @description
 */
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    List<Brand> findByIdIn(List<Integer> brandIdList);

    @Query(value = "select id, name from brand", nativeQuery = true)
    List<BrandDTO> findAllIdAndName();

    Brand findByName(String name);

//    Page<Brand> findAll(Pageable pageable);

    List<Brand> findByNameLike(String name);

}
