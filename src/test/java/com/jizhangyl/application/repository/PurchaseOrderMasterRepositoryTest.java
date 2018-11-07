package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderMaster;
import com.jizhangyl.application.repository.primary.PurchaseOrderMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/10/8 10:28
 * @description
 */
@Slf4j
@Component
public class PurchaseOrderMasterRepositoryTest extends MainApplicationTests {

    @Autowired
    private PurchaseOrderMasterRepository purchaseOrderMasterRepository;

    @Test
    public void findByCriteria() {
        List<Integer> orderStatusList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<PurchaseOrderMaster> result = purchaseOrderMasterRepository.findByCriteria("", orderStatusList, pageRequest);
        log.info("content = {}", result.getContent());
        log.info("totalPages = {}", result.getTotalPages());
        log.info("result = {}", result);
    }
}