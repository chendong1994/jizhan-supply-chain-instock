package com.jizhangyl.application.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.jizhangyl.application.enums.ResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.primary.Brand;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.BrandForm;
import com.jizhangyl.application.service.BrandService;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨贤达
 * @date 2018/8/20 10:23
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 查询所有品牌名称
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResultVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Brand> brandPage = brandService.findAll(pageRequest);

        Map<String, Object> map = new HashMap<>();
        map.put("data", brandPage.getContent());
        map.put("totalPage", brandPage.getTotalPages());

        return ResultVOUtil.success(map);
    }

    @PostMapping("/save")
    public ResultVO save(@Valid BrandForm brandForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        List<Brand> brandList = brandService.findByBrandName(brandForm.getName());
        if (!CollectionUtils.isEmpty(brandList)) {
            throw new GlobalException(ResultEnum.BRAND_IS_EXIST.getCode(),
                    String.format(ResultEnum.BRAND_IS_EXIST.getMessage(), brandForm.getName()));
        }

        Brand brand = new Brand();
        BeanUtils.copyProperties(brandForm, brand);
        Brand result = brandService.save(brand);

        return ResultVOUtil.success();
    }

    /**
     * 品牌更新
     * @param brandForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/update")
    public ResultVO update(@Valid BrandForm brandForm,
                         BindingResult bindingResult) {
        if (brandForm.getId() == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        List<Brand> brandList = brandService.findByBrandName(brandForm.getName());
        if (!CollectionUtils.isEmpty(brandList)) {
            throw new GlobalException(ResultEnum.BRAND_IS_EXIST.getCode(),
                    String.format(ResultEnum.BRAND_IS_EXIST.getMessage(), brandForm.getName()));
        }

        Brand brand = new Brand();
        BeanUtils.copyProperties(brandForm, brand);
        Brand result = brandService.save(brand);
        return ResultVOUtil.success();
    }

    @GetMapping("/delete")
    public ResultVO delete(@RequestParam("id") Integer id) {
        if (id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        brandService.delete(id);
        return ResultVOUtil.success();
    }

    /**
     * 按照名字模糊查询
     * @param name
     * @return
     */
    @GetMapping("/findByName")
    public ResultVO findByName(@RequestParam("name") String name) {
        if (StringUtils.isEmpty(name)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<Brand> brandList = brandService.findByBrandName(name);
        return ResultVOUtil.success(brandList);
    }
}
