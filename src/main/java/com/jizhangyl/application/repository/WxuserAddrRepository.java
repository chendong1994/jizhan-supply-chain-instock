package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.WxuserAddr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/5 12:55
 * @description
 */
public interface WxuserAddrRepository extends JpaRepository<WxuserAddr, Integer> {

    //    List<WxuserAddr> save(List<WxuserAddr> wxuserAddrList);

    @Query(value = "select id, wxuser_id, receiver_nickname, receiver, phone, province, city, area, detail_addr, addr_label, is_default from wxuser_addr where receiver_nickname like %:name% or receiver like %:name%", nativeQuery = true)
    List<WxuserAddr> findByReceiverOrReceiverNickname(@Param("name") String name);

    Page<WxuserAddr> findAllByOpenid(String openid, Pageable pageable);

    List<WxuserAddr> findAllByOpenidOrderByCreateTimeDesc(String openid);

    List<WxuserAddr> findByIdIn(List<Integer> idList);
}
