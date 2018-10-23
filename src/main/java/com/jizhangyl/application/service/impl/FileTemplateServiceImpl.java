package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.FileTemplate;
import com.jizhangyl.application.repository.FileTemplateRepository;
import com.jizhangyl.application.service.FileTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨贤达
 * @date 2018/9/5 17:53
 * @description
 */
@Service
public class FileTemplateServiceImpl implements FileTemplateService {

    @Autowired
    private FileTemplateRepository fileTemplateRepository;

    @Override
    public FileTemplate findByTemplateNo(Integer templateNo) {
        return fileTemplateRepository.findByTemplateNo(templateNo);
    }

    @Override
    public FileTemplate save(FileTemplate template) {
        return fileTemplateRepository.save(template);
    }
}
