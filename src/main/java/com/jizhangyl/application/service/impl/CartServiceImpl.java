package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.VO.CartVO;
import com.jizhangyl.application.dataobject.primary.Cart;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.Shop;
import com.jizhangyl.application.enums.CartEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.CartRepository;
import com.jizhangyl.application.service.CartService;
import com.jizhangyl.application.service.ShopService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/1 20:02
 * @description
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ShopService shopService;

    @Override
    public List<CartVO> findByOpenid(String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<Cart> cartList = cartRepository.findByOpenid(openid);
        List<Integer> productIdList = cartList.stream().map(e -> e.getProductId()).collect(Collectors.toList());
        List<Shop> shopList = shopService.findByIdIn(productIdList);

        List<CartVO> cartVOList = cartList.stream().map(e -> {

            Shop shop = null;
            for (Shop s : shopList) {
                if (s.getId().equals(e.getProductId())) {
                    shop = s;
                    break;
                }
            }

            CartVO cartVO = new CartVO();
            BeanUtils.copyProperties(e, cartVO);

            if (shop != null) {
                String[] ignoreProperties = {"id", "productId", "productName", "productPrice", "productQuantity", "productStatus"};
                BeanUtils.copyProperties(shop, cartVO, ignoreProperties);
            }

            return cartVO;
        }).collect(Collectors.toList());
        return cartVOList;
    }

    @Override
    public void delete(Integer cartCellId) {
        if (cartCellId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
//        Cart cart = cartRepository.findOne(cartCellId);
        Cart cart = cartRepository.getOne(cartCellId);
        if (cart == null) {
            throw new GlobalException(ResultEnum.CART_ERROR);
        }
//        cartRepository.delete(cartCellId);
        cartRepository.deleteById(cartCellId);
    }

    /**
     * 商品数量增加
     * @param cartCellId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void increase(Integer cartCellId) {
        // 1. 查询商品库存
//        Cart cart = cartRepository.findOne(cartCellId);
        Cart cart = cartRepository.getOne(cartCellId);
        if (cart == null) {
            throw new GlobalException(ResultEnum.CART_ERROR);
        }
        Integer productId = cart.getProductId();
        Shop shop = shopService.findOne(productId);
        if (shop == null) {
            throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        Integer stock = shop.getShopCount();
        Integer currentQuantity = cart.getProductQuantity() + CartEnum.PRODUCT_QUANTITY_UNIT.getCode();
        if (currentQuantity > stock) {
            throw new GlobalException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH);
        }
        cart.setProductQuantity(currentQuantity);
        cartRepository.save(cart);
    }

    /**
     * 商品数量减少
     * @param cartCellId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reduce(Integer cartCellId) {
//        Cart cart = cartRepository.findOne(cartCellId);
        Cart cart = cartRepository.getOne(cartCellId);
        if (cart == null) {
            throw new GlobalException(ResultEnum.CART_ERROR);
        }
        Integer result = cart.getProductQuantity() - 1;
        if (result == 0) {
            throw new GlobalException(ResultEnum.PRODUCT_QUANTITY_ERROR);
        } else if (result < 0) {
            throw new GlobalException(ResultEnum.CART_ERROR);
        }
        cart.setProductQuantity(result);
        cartRepository.save(cart);
    }

    /**
     * 购物车新增商品
     * @param productId
     * @param openid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(Integer productId, String openid) {
        // 1. 判断商品信息
        // 2. 判断库存
        // 3. 扣库存
        // 4. 入库
        if (productId == null || StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        // 判断商品是否下架
        Shop shop = shopService.findOne(productId);
        if (shop == null) {
            throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        // 判断库存是否已经为 0
        Integer result = shop.getShopCount() - CartEnum.PRODUCT_QUANTITY_UNIT.getCode();
        if (result < 0) {
            throw new GlobalException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH);
        }

        /**
         * 判断购物车是否已经有该商品
         */
        boolean flag = false;
        List<Cart> cartList = cartRepository.findByOpenid(openid);
        for (Cart cart : cartList) {
            if (cart.getProductId().equals(productId)) {
                cart.setProductQuantity(cart.getProductQuantity() + CartEnum.PRODUCT_QUANTITY_UNIT.getCode());
                cartRepository.save(cart);
                flag = true;
                break;
            }
        }
        if (!flag) {
            Cart cart = new Cart();
            cart.setProductId(productId);
            cart.setOpenid(openid);
            cart.setProductQuantity(CartEnum.PRODUCT_QUANTITY_UNIT.getCode());
            cart.setProductName(shop.getShopName());
            cart.setProductPrice(shop.getShopGprice());
            cart.setCreateTime(new Date());
            cart.setUpdateTime(new Date());
            cartRepository.save(cart);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void clearCart(String openid, List<OrderDetail> orderDetailList) {
        List<Cart> cartList = cartRepository.findByOpenid(openid);
        if (CollectionUtils.isEmpty(cartList)) {
            throw new GlobalException(ResultEnum.CART_EMPTY);
        }
        List<Cart> delCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            Integer productId = Integer.valueOf(orderDetail.getProductId());
            for (Cart cart : cartList) {
                if (productId.equals(cart.getProductId())) {
                    delCartList.add(cart);
                }
            }
        }
//        cartRepository.delete(delCartList);
        cartRepository.deleteAll(delCartList);
    }
}
