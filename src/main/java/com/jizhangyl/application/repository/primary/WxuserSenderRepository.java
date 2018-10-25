package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.WxuserSender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/7 16:48
 * @description
 */
public interface WxuserSenderRepository extends JpaRepository<WxuserSender, Integer> {

    Page<WxuserSender> findAllByOpenid(String openid, Pageable pageable);

    List<WxuserSender> findAllByOpenidOrderByCreateTimeDesc(String openid);

    List<WxuserSender> findByOpenid(String openid);

    WxuserSender findByOpenidAndIsDefault(String openid, Integer status);

    List<WxuserSender> findByOpenidIn(List<String> openidList);

    List<WxuserSender> findByIdIn(List<Integer> idList);

}
