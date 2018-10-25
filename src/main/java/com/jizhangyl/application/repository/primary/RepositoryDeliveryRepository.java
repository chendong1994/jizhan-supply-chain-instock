package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.RepositoryDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/26 14:38
 * @description
 */
public interface RepositoryDeliveryRepository extends JpaRepository<RepositoryDelivery, String> {

    List<RepositoryDelivery> findByCreateTimeBetween(Date beginTime, Date endTime);
}
