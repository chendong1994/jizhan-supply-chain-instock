package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.FileTemplate;
import com.jizhangyl.application.enums.FileTemplateEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.FileTemplateService;
import com.jizhangyl.application.utils.FileUploadUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 杨贤达
 * @date 2018/9/5 17:29
 * @description
 */
@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private FileTemplateService fileTemplateService;

    /**
     * 代购导入订单的 excel 模板上传
     * @param file
     * @return
     */
    @PostMapping("/uploadBuyer")
    public ResultVO uploadBuyer(@RequestParam("template")MultipartFile file) {

        upload(file, FileTemplateEnum.BUYER);

        return ResultVOUtil.success();
    }

    /**
     * 发货 excel 模板上传
     * @param file
     * @return
     */
    @PostMapping("/uploadDelivery")
    public ResultVO uploadDelivery(@RequestParam("template")MultipartFile file) {

        upload(file, FileTemplateEnum.DELIVERY);

        return ResultVOUtil.success();
    }


    /**
     * 代购导入订单的 excel 模板下载
     * @return
     */
    @GetMapping("/downloadBuyer")
    public ResultVO downloadBuyer() {
        return ResultVOUtil.success(download(FileTemplateEnum.BUYER));
    }

    /**
     * 发货 excel 模板下载
     * @return
     */
    @GetMapping("/downloadDelivery")
    public ResultVO downloadDelivery() {
        return ResultVOUtil.success(download(FileTemplateEnum.DELIVERY));
    }



    @Transactional(rollbackFor = Exception.class)
    public void upload(MultipartFile file, FileTemplateEnum templateEnum) {

        String fileUrl = fileUploadUtil.upload(file);

        FileTemplate fileTemplate = fileTemplateService.findByTemplateNo(templateEnum.getNo());

        String fileAddr = null;

        if (fileTemplate != null && !StringUtils.isEmpty(fileTemplate.getTemplateUrl())) {
            fileAddr = fileTemplate.getTemplateUrl();
        }

        FileTemplate result = null;

        if (fileTemplate == null) {
            FileTemplate template = new FileTemplate();
            template.setTemplateNo(templateEnum.getNo());
            template.setTemplateName(templateEnum.getName());
            template.setTemplateUrl(fileUrl);
            result = fileTemplateService.save(template);
        } else {
            fileTemplate.setTemplateUrl(fileUrl);
            result = fileTemplateService.save(fileTemplate);
        }

        if (result == null) {
            throw new GlobalException(ResultEnum.TEMPLATE_SAVE_FAIL);
        }

        // 如果存在旧的模板，则删除
        if (!StringUtils.isEmpty(fileAddr)) {
            String fileName = fileAddr.substring(fileAddr.lastIndexOf("/") + 1);
            fileUploadUtil.delete(fileName);
        }
    }

    /**
     * 暂时不采用此方式下载
     * @param fileTemplateEnum
     */
    public String download(FileTemplateEnum fileTemplateEnum) {
        FileTemplate fileTemplate = fileTemplateService.findByTemplateNo(fileTemplateEnum.getNo());
        if (fileTemplate == null) {
            throw new GlobalException(ResultEnum.TEMPLATE_NOT_EXIST);
        }
        return fileTemplate.getTemplateUrl();
    }
}
