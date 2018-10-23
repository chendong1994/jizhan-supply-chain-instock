package com.jizhangyl.application.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.jizhangyl.application.enums.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jizhangyl.application.VO.CateVO;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.Cate;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.CateForm;
import com.jizhangyl.application.service.CateService;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨贤达
 * @date 2018/8/1 17:22
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/cate")
public class CateController {

    @Autowired
    private CateService cateService;

//    @RequiresPermissions("shop:cate:list")
    @GetMapping("/list")
    public ResultVO list() {
        List<CateVO> cateVOList = cateService.findAll();
        return ResultVOUtil.success(cateVOList);
    }

//    @RequiresPermissions("shop:cate:delete")
    @GetMapping("/delete")
    public ResultVO delete(@RequestParam("cateIds") String cateIds) {
        if (StringUtils.isEmpty(cateIds)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        try {
            if (cateIds.contains("-")) {
                String[] ids = cateIds.split("-");
                List<String> cateIdList = Stream.of(ids).collect(Collectors.toList());
                List<Integer> list = cateIdList.stream().map(e -> {
                    Integer cateId = Integer.parseInt(e);
                    return cateId;
                }).collect(Collectors.toList());
                cateService.delete(list);
            } else {
                Integer cateId = Integer.parseInt(cateIds);
                cateService.delete(cateId);
            }
        } catch (Exception e) {
            log.error("【类目操作】类目删除错误：{}", e);
            throw new GlobalException(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
        }
        return ResultVOUtil.success();
    }

//    @RequiresPermissions("shop:cate:update")
    @PostMapping("/update")
    public ResultVO update(@Valid CateForm cateForm,
                           BindingResult bindingResult) {
        if (cateForm.getId() == null || bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        cateService.update(cateForm);
        return ResultVOUtil.success();
    }

//    @RequiresPermissions("shop:cate:add")
    @PostMapping("/save")
    public ResultVO save(@Valid CateForm cateForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        cateService.save(cateForm);
        return ResultVOUtil.success();
    }

//    @RequiresPermissions("shop:cate:list")
    @GetMapping("/getByCateName")
    public ResultVO getByCateName(@RequestParam("cateName") String cateName) {
        if (StringUtils.isEmpty(cateName)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<Cate> cateLIst = cateService.findByNameLike(cateName);
        return ResultVOUtil.success(cateLIst);
    }
}
