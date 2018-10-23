package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/8/15 15:40
 * @description
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    
    UserInfo findByOpenid(String openid);
    
    UserInfo findByEmpNo(String empNo);
}
