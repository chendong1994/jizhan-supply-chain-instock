package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.CustomerCert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 16:39
 * @description
 */
public interface CustomerCertRepository extends JpaRepository<CustomerCert, Integer> {

    Page<CustomerCert> findByCheckStatus(Integer checkStatus, Pageable pageable);

    List<CustomerCert> findByOpenidAndCheckStatus(String openid, Integer checkStatus);

    List<CustomerCert> findByIdNum(String idNum);

    /**
     * 当某个用户对订单上传了多次证件信息会产生多个 CustomerCert
     * @param expressNum
     * @param checkStatus
     * @return
     */
    List<CustomerCert> findByExpressNumAndCheckStatus(String expressNum, Integer checkStatus);

    List<CustomerCert> findByOpenidAndSaveStatus(String openid, Integer saveStatus);

    List<CustomerCert> findByExpressNumAndCheckStatusIn(String expressNum, List<Integer> checkStatusList);
}
