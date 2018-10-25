package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.Cate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:38
 * @description
 */
public interface CateRepository extends JpaRepository<Cate, Integer> {

    List<Cate> findByIdIn(List<Integer> cateIdList);

    Cate findByName(String name);

    List<Cate> findByNameLike(String name);

}
