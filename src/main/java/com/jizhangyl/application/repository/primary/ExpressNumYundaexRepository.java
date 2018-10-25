package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ExpressNumYundaex;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumYundaexRepository extends JpaRepository<ExpressNumYundaex, Integer> {

    ExpressNumYundaex findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumYundaex findByExpNum(String expNum);
}
