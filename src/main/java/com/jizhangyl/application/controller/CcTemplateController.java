package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.primary.CcTemplate;
import com.jizhangyl.application.service.CcTemplateService;
import com.jizhangyl.application.utils.ResultVOUtil;
import com.jizhangyl.application.utils.excel.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/10/17 20:48
 * @description
 */
@RestController
@RequestMapping("/cc/template")
public class CcTemplateController {

    @Autowired
    private CcTemplateService ccTemplateService;

    @PostMapping("/import")
    public ResultVO importExcel(@RequestParam("template") MultipartFile file) throws Exception {
        ExcelUtil<CcTemplate> excelUtil = new ExcelUtil<>(CcTemplate.class);
        List<CcTemplate> ccTemplateList = excelUtil.importExcel("海关清关", file.getInputStream());
        ccTemplateService.save(ccTemplateList);
        return ResultVOUtil.success();
    }

    @GetMapping("/subCategorys")
    public ResultVO secondaryCategorys(@RequestParam(value = "categoryType", required = false) Integer categoryType,
                                       @RequestParam(value = "categoryName", required = false) String categoryName) {
        String category = null;
        List<CcTemplate> templateList = null;
        List<String> categoryNameList = new ArrayList<>();

        if (categoryType.equals(1)) {
            templateList = ccTemplateService.getAllCategorys();
        } else {

            switch (categoryType) {
                case 2:
                    category = "primaryCategory";
                    break;
                case 3:
                    category = "secondaryCategory";
                    break;
                case 4:
                    category = "thirdCategory";
                    break;
                case 5:
                    category = "fourthCategory";
                    break;
            }

            templateList = ccTemplateService.getByCategory(category, categoryName);
        }

        if (!CollectionUtils.isEmpty(templateList)) {

            switch (categoryType) {
                case 1:
                    categoryNameList = templateList.stream()
                            .map(e -> e.getPrimaryCategory())
                            .distinct()
                            .filter(e -> e != null)
                            .collect(Collectors.toList());
                    break;
                case 2:
                    categoryNameList = templateList.stream()
                            .map(e -> e.getSecondaryCategory())
                            .distinct()
                            .filter(e -> e != null)
                            .collect(Collectors.toList());
                    break;
                case 3:
                    categoryNameList = templateList.stream()
                            .map(e -> e.getThirdCategory())
                            .distinct()
                            .filter(e -> e != null)
                            .collect(Collectors.toList());
                    break;
                case 4:
                    categoryNameList = templateList.stream()
                            .map(e -> e.getFourthCategory())
                            .distinct()
                            .filter(e -> e != null)
                            .collect(Collectors.toList());
                    break;
                case 5:
                    categoryNameList = templateList.stream()
                            .map(e -> e.getFifthCategory())
                            .distinct()
                            .filter(e -> e != null)
                            .collect(Collectors.toList());
                    break;
            }
        }

        return ResultVOUtil.success(categoryNameList);
    }

    @GetMapping("/getTemplateByCategorys")
    public ResultVO getTemplateByCategorys(@RequestParam("primaryCategory") String primaryCategory,
                                           @RequestParam("secondaryCategory") String secondaryCategory,
                                           @RequestParam("thirdCategory") String thirdCategory,
                                           @RequestParam("fourthCategory") String fourthCategory,
                                           @RequestParam("fifthCategory") String fifthCategory) {
        if (StringUtils.isEmpty(primaryCategory) || primaryCategory.equals("------")) {
            primaryCategory = "";
        }

        if (StringUtils.isEmpty(secondaryCategory) || secondaryCategory.equals("------")) {
            secondaryCategory = "";
        }

        if (StringUtils.isEmpty(thirdCategory) || thirdCategory.equals("------")) {
            thirdCategory = "";
        }

        if (StringUtils.isEmpty(fourthCategory) || fourthCategory.equals("------")) {
            fourthCategory = "";
        }

        if (StringUtils.isEmpty(fifthCategory) || fifthCategory.equals("------")) {
            fifthCategory = "";
        }

        List<CcTemplate> ccTemplateList = ccTemplateService.findByPrimaryCategoryAndSecondaryCategoryAndThirdCategoryAndFourthCategoryAndFifthCategory(
                primaryCategory, secondaryCategory, thirdCategory, fourthCategory, fifthCategory);

        if (ccTemplateList.size() == 1) {
            return ResultVOUtil.success(ccTemplateList.get(0));
        } else {
            return ResultVOUtil.success("");
        }
    }
}
