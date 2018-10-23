package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.ExpressNum;
import com.jizhangyl.application.dataobject.ExpressNumCc;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumCcRepository extends JpaRepository<ExpressNumCc, Integer> {

    ExpressNumCc findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumCc findByExpNum(String expNum);
}
