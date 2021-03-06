package com.jizhangyl.application.repository.secondary;

import com.jizhangyl.application.dataobject.secondary.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/8/15 15:40
 * @description
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    
    UserInfo findByOpenid(String openid);

    UserInfo findByUnionid(String unionid);
    
    UserInfo findByEmpNo(String empNo);
}
