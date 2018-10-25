package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ExpressNumYto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumYtoRepository extends JpaRepository<ExpressNumYto, Integer> {

    ExpressNumYto findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumYto findByExpNum(String expNum);
}
