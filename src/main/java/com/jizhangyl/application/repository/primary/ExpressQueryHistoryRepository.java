package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.ExpressQueryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/24 11:29
 * @description
 */
public interface ExpressQueryHistoryRepository extends JpaRepository<ExpressQueryHistory, Integer> {

    List<ExpressQueryHistory> findByOpenid(String openid);

    List<ExpressQueryHistory> findByOpenidOrderByQueryDateDesc(String openid);

    ExpressQueryHistory findByOpenidAndExpNum(String openid, String expNum);
    
    /**
     * 查找在指定的物流单号集合内的物流单号信息
     * @param expNums 存储指定的物流单号
     * @return 在指定的物流单号集合内的物流单号信息
     */
    List<ExpressQueryHistory> findByExpNumIn(List<String> expNums);
}
