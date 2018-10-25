package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ExpressNumSto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumStoRepository extends JpaRepository<ExpressNumSto, Integer> {

    ExpressNumSto findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumSto findByExpNum(String expNum);
}
