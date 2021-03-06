package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:08
 * @description
 */
public interface ShopRepository extends JpaRepository<Shop, Integer> {

    List<Shop> findByCateId(Integer cateId);

    Page<Shop> findByCateId(Integer cateId, Pageable pageable);

    Page<Shop> findByCateIdAndShopStatus(Integer cateId, Integer status, Pageable pageable);

    Page<Shop> findByShopStatus(Integer status, Pageable pageable);

    Shop findByShopJan(String shopJan);

    List<Shop> findByShopNameLike(String shopName);

    List<Shop> findByIdIn(List<Integer> idList);

    Shop findByPackCode(String packCode);

    @Query(nativeQuery = true, value = "SELECT * FROM `shop` WHERE CONCAT(IFNULL(`shop_name`, '')," +
            "IFNULL(`shop_jan`, ''), IFNULL(`customs_product_id`, ''), IFNULL(`pack_code`, '')) LIKE %:param%")
    List<Shop> findByCriteria(@Param("param") String param);

    /*@Query(nativeQuery = true, value = "")
    void syncDataToOthers();*/
}
