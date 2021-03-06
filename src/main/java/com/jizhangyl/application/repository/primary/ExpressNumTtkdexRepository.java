package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ExpressNumTtkdex;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumTtkdexRepository extends JpaRepository<ExpressNumTtkdex, Integer> {

    ExpressNumTtkdex findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumTtkdex findByExpNum(String expNum);
}
