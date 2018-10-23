package com.jizhangyl.application.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jizhangyl.application.VO.ShopDetailVO;
import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.Shop;
import com.jizhangyl.application.dto.ShopDto;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:09
 * @description
 */
public interface ShopService {

//    Page<ShopVO> findAll(Pageable pageable);
    Page<Shop> findAll(Pageable pageable);

//    Page<ShopVO> findUp(Pageable pageable);
    Page<Shop> findUp(Pageable pageable);

    List<ShopDto> findAll();

    List<Shop> getAll();

    List<ShopVO> findByCateId(Integer cateId);

    Page<ShopVO> findByCateId(Integer cateId, Pageable pageable);

    ShopDetailVO detail(Integer shopId);

    Shop findOne(Integer productId);

    Shop save(Shop shop);

    Shop findByShopJan(String shopJan);

    // 减库存
    void decreaseStock(List<OrderDetail> orderDetailList);

    // 加库存
    void increaseStock(List<OrderDetail> orderDetailList);

    List<Shop> saveInBatch(List<ShopDto> shopDtoList);

    void delete(Integer id);

    void up(Integer productId);

    void down(Integer productId);

    List<Shop> findByName(String name);

    // <=========修复冲突========>
    List<Shop> findByIdIn(List<Integer> shopIdList);

    Shop findByPackCode(String packCode);

    List<Shop> findByCriteria(String param);

    void productDataSync();

    /**
     * 查询在指定时间段内的商品的销量(不包括已经取消的)
     * @return 指定时间段内的商品的销量
     */
    int findShopSalesNumBetween(Date beginDate, Date endDate, String productId);
}
