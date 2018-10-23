package com.jizhangyl.application.repository;

import com.jizhangyl.application.MainApplicationTests;
import com.jizhangyl.application.dataobject.CcTemplate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/10/18 11:19
 * @description
 */
@Component
public class CcTemplateRepositoryTest extends MainApplicationTests {

    @Autowired
    private CcTemplateRepository ccTemplateRepository;

    @Test
    public void getCategoryByName() {

        String category = "primaryCategory";
        String categoryName = "乐器";

        Specification<CcTemplate> specification = (Root<CcTemplate> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(category)) {
                predicateList.add(criteriaBuilder.equal(root.get(category), categoryName));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };

        List<CcTemplate> ccTemplateList = ccTemplateRepository.findAll(specification);

        System.out.println(ccTemplateList);
    }
}