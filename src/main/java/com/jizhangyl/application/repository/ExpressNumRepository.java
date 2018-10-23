package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.ExpressNum;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:49
 * @description
 */
public interface ExpressNumRepository extends JpaRepository<ExpressNum, Integer> {

    ExpressNum findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNum findByExpNum(String expNum);
}
