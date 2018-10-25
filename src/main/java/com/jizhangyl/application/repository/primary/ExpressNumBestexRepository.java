package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ExpressNumBestex;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/10/16 19:14
 * @description
 */
public interface ExpressNumBestexRepository extends JpaRepository<ExpressNumBestex, Integer> {

    ExpressNumBestex findTop1ByStatus(Integer status);

    Integer countByStatus(Integer status);

    ExpressNumBestex findByExpNum(String expNum);
}
