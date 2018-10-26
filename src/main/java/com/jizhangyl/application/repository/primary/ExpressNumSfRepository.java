package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ExpressNumSf;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumSfRepository extends JpaRepository<ExpressNumSf, Integer> {

    ExpressNumSf findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumSf findByExpNum(String expNum);
}
