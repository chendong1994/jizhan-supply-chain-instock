package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.CartVO;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.CartService;
import com.jizhangyl.application.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/1 20:00
 * @description
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    public ResultVO list(@RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<CartVO> cartVOList = cartService.findByOpenid(openid);
        return ResultVOUtil.success(cartVOList);
    }

    @GetMapping("/increase")
    public ResultVO update(@RequestParam("cartCellId") Integer cartCellId,
                           @RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid) || cartCellId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        cartService.increase(cartCellId);

        List<CartVO> cartVOList = cartService.findByOpenid(openid);
        return ResultVOUtil.success(cartVOList);
    }

    @GetMapping("/reduce")
    public ResultVO reduce(@RequestParam("cartCellId") Integer cartCellId,
                           @RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid) || cartCellId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        cartService.reduce(cartCellId);

        List<CartVO> cartVOList = cartService.findByOpenid(openid);
        return ResultVOUtil.success(cartVOList);
    }

    @GetMapping("/delete")
    public ResultVO delete(@RequestParam("cartCellId") Integer cartCellId,
                           @RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid) || cartCellId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        cartService.delete(cartCellId);
        List<CartVO> cartVOList = cartService.findByOpenid(openid);
        return ResultVOUtil.success(cartVOList);
    }

    @PostMapping("/add")
    public ResultVO add(@RequestParam("productId") Integer productId,
                           @RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid) || productId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        cartService.add(productId, openid);
        List<CartVO> cartVOList = cartService.findByOpenid(openid);
        return ResultVOUtil.success(cartVOList);
    }
}
