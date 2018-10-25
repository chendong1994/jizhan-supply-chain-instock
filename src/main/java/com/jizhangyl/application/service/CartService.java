package com.jizhangyl.application.service;

import com.jizhangyl.application.VO.CartVO;
import com.jizhangyl.application.dataobject.primary.OrderDetail;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 19:55
 * @description
 */
public interface CartService {
    List<CartVO> findByOpenid(String openid);

    void delete(Integer cartCellId);

    void increase(Integer cartCellId);

    void reduce(Integer cartCellId);

    void add(Integer productId, String openid);

    void clearCart(String openid, List<OrderDetail> orderDetailList);
}
