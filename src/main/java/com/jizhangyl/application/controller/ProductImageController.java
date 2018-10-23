package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.service.ProductImageService;
import com.jizhangyl.application.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨贤达
 * @date 2018/9/11 16:16
 * @description
 */
@RestController
@RequestMapping("/product/image")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @GetMapping("/upload")
    public ResultVO upload(@RequestParam("imageUrl") String imageUrl, @RequestParam("imageOrder") Integer imageOrder) {

        return ResultVOUtil.success();
    }
}
