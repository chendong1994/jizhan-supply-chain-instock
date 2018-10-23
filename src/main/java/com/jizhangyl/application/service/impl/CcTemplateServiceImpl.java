package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.CcTemplate;
import com.jizhangyl.application.repository.CcTemplateRepository;
import com.jizhangyl.application.service.CcTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/10/17 20:49
 * @description
 */
@Service
public class CcTemplateServiceImpl implements CcTemplateService {

    @Autowired
    private CcTemplateRepository ccTemplateRepository;

    @Override
    public CcTemplate save(CcTemplate ccTemplate) {
        return ccTemplateRepository.save(ccTemplate);
    }

    @Override
    @Transactional
    public List<CcTemplate> save(List<CcTemplate> ccTemplateList) {
        List<CcTemplate> dataList = new ArrayList<>(100);
        for (CcTemplate ccTemplate : ccTemplateList) {
            if (dataList.size() == 100) {
                ccTemplateRepository.save(dataList);
                dataList.clear();
            }
            dataList.add(ccTemplate);
        }
        if (!dataList.isEmpty()) {
            ccTemplateRepository.save(dataList);
        }
        return ccTemplateRepository.save(ccTemplateList);
    }

    @Override
    public List<CcTemplate> getByCategory(String category, String categoryName) {
        Specification<CcTemplate> specification = (Root<CcTemplate> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(category)) {
                predicateList.add(criteriaBuilder.equal(root.get(category), categoryName));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };
        List<CcTemplate> ccTemplateList = ccTemplateRepository.findAll(specification);
        return ccTemplateList;
    }

    @Override
    public List<CcTemplate> getAllCategorys() {
        return ccTemplateRepository.findAll();
    }

    @Override
    public List<CcTemplate> findByPrimaryCategoryAndSecondaryCategoryAndThirdCategoryAndFourthCategoryAndFifthCategory(String primaryCategory, String secondaryCategory, String thirdCategory, String fourthCategory, String fifthCategory) {
        return ccTemplateRepository.findByPrimaryCategoryAndSecondaryCategoryAndThirdCategoryAndFourthCategoryAndFifthCategory(
                primaryCategory, secondaryCategory, thirdCategory, fourthCategory, fifthCategory);
    }
}
