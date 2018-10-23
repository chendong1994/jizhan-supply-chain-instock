package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.Brand;
import com.jizhangyl.application.dataobject.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 19:55
 * @description
 */
public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findByOpenid(String openid);

    List<Cart> deleteByOpenidAndProductId(String openid, Integer productId);
}
