package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.RepositoryProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/3 16:08
 * @description
 */
public interface RepositoryProductRepository extends JpaRepository<RepositoryProduct, Integer> {

    RepositoryProduct findByProductJancode(String productJancode);

    List<RepositoryProduct> findByProductJancodeIn(List<String> productJancodeList);

    List<RepositoryProduct> findByProductIdIn(List<Integer> productIdList);

    RepositoryProduct findByProductId(Integer productId);

    @Query(nativeQuery = true, value = "select * from `repository_product` r join `shop` s on " +
            "s.id = r.product_id where concat(ifnull(r.`product_name`, ''), ifnull(r.`product_jancode`, ''), " +
            "ifnull(r.`pack_code`, '')) like %:param% and s.shop_status = :status")
    List<RepositoryProduct> findByCriteria(@Param("param") String param, @Param("status") Integer status);

    @Query(nativeQuery = true, value = "select r.* from `repository_product` r join `shop` s on " +
            "s.id = r.product_id where concat(ifnull(r.`product_name`, ''), ifnull(r.`product_jancode`, ''), " +
            "ifnull(r.`pack_code`, '')) like %:param% and s.shop_status = :status order by ?#{#pageable}")
    Page<RepositoryProduct> findByCriteria(@Param("param") String param,
                                           @Param("status") Integer status,
                                           Pageable pageable);
}
