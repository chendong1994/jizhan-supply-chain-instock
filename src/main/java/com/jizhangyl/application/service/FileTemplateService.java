package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.FileTemplate;

/**
 * @author 杨贤达
 * @date 2018/9/5 17:52
 * @description
 */
public interface FileTemplateService {

    FileTemplate findByTemplateNo(Integer templateNo);

    FileTemplate save(FileTemplate template);
}
