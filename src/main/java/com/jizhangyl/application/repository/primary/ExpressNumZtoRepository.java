package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ExpressNumZto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumZtoRepository extends JpaRepository<ExpressNumZto, Integer> {

    ExpressNumZto findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumZto findByExpNum(String expNum);
}
