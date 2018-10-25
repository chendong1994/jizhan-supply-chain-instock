package com.jizhangyl.application.repository.primary;

import com.jizhangyl.application.dataobject.primary.FileTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 杨贤达
 * @date 2018/9/5 17:51
 * @description
 */
public interface FileTemplateRepository extends JpaRepository<FileTemplate, Integer> {

    FileTemplate findByTemplateNo(Integer templateNo);
}
