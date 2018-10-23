package com.jizhangyl.application.repository;

import com.jizhangyl.application.dataobject.CcTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/10/17 20:47
 * @description
 */
public interface CcTemplateRepository extends
        JpaRepository<CcTemplate, String>, JpaSpecificationExecutor {
    List<CcTemplate> findByPrimaryCategoryAndSecondaryCategoryAndThirdCategoryAndFourthCategoryAndFifthCategory(
            String primaryCategory, String secondaryCategory,
            String thirdCategory, String fourthCategory, String fifthCategory);
}
