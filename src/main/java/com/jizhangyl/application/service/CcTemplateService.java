package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.primary.CcTemplate;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/10/17 20:48
 * @description
 */
public interface CcTemplateService {

    CcTemplate save(CcTemplate ccTemplate);

    List<CcTemplate> save(List<CcTemplate> ccTemplateList);

    List<CcTemplate> getByCategory(String category, String categoryName);

    List<CcTemplate> getAllCategorys();

    List<CcTemplate> findByPrimaryCategoryAndSecondaryCategoryAndThirdCategoryAndFourthCategoryAndFifthCategory(
            String primaryCategory, String secondaryCategory,
            String thirdCategory, String fourthCategory, String fifthCategory);
}
