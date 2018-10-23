package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.ExpressNumJp;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumJpRepository extends JpaRepository<ExpressNumJp, Integer> {

    ExpressNumJp findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumJp findByExpNum(String expNum);
}
