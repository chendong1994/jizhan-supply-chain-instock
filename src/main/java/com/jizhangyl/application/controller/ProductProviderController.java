package com.jizhangyl.application.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jizhangyl.application.VO.ProviderProductRelatVo;
import com.jizhangyl.application.VO.ProviderVo;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.primary.ProductProvider;
import com.jizhangyl.application.dataobject.primary.ProviderProductLog;
import com.jizhangyl.application.dataobject.primary.ProviderProductRelat;
import com.jizhangyl.application.enums.CurrencyEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.ProductProviderForm;
import com.jizhangyl.application.form.ProviderProductRelatForm;
import com.jizhangyl.application.service.ProductProviderService;
import com.jizhangyl.application.service.ProviderProductLogService;
import com.jizhangyl.application.service.ProviderProductRelatService;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨贤达
 * @date 2018/7/27 9:59
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/product/provider")
public class ProductProviderController {

    @Autowired
    private ProductProviderService productProviderService;

    @Autowired
    private ProviderProductLogService providerProductLogService;

    @Autowired
    private ProviderProductRelatService providerProductRelatService;

    @GetMapping("/findAll")
    public ResultVO findAll() {
        List<ProductProvider> productProviderList = productProviderService.findAll();
        List<ProviderVo> providerVoList = productProviderList.stream()
                .map(e -> new ProviderVo(e.getProviderId(), e.getCompanyName()))
                .collect(Collectors.toList());
        return ResultVOUtil.success(providerVoList);
    }

    @PostMapping("/save")
    public ResultVO save(@Valid ProductProviderForm productProviderForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【参数校验不通过】productProviderForm = {}", productProviderForm);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(), bindingResult.getFieldError().getDefaultMessage()));
        }
        ProductProvider productProvider = new ProductProvider();

        try {
            if (productProviderForm.getProviderId() != null) {
                productProvider = productProviderService.findOne(productProviderForm.getProviderId());
            }
            BeanUtils.copyProperties(productProviderForm, productProvider);
            if (productProvider.getCurrency() == null) {
                productProvider.setCurrency(CurrencyEnum.JPY.getCode());
            }
            productProviderService.save(productProvider);
        } catch (GlobalException e) {
            throw new GlobalException(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
        }

        return ResultVOUtil.success();
    }

    @GetMapping("/list")
    public ResultVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ProductProvider> productProviderPage = productProviderService.findAll(pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", productProviderPage.getContent());
        result.put("totalPage", productProviderPage.getTotalPages());
        return ResultVOUtil.success(result);
    }

    @GetMapping("/index")
    public ResultVO index(@RequestParam(value = "providerId", required = false) Integer providerId) {
        if (providerId != null) {
            ProductProvider productProvider = productProviderService.findOne(providerId);
            return ResultVOUtil.success(productProvider);
        }
        return ResultVOUtil.success();
    }

    @GetMapping("/delete")
    public ResultVO delete(@RequestParam("providerId") Integer providerId) {
        try {
            if (providerId != null) {
                productProviderService.delete(providerId);
            } else {
                throw new GlobalException(ResultEnum.PARAM_EMPTY);
            }
        } catch (GlobalException e) {
            throw new GlobalException(ResultEnum.SERVER_ERROR);
        }
        return ResultVOUtil.success();
    }

    /**
     * 根据供应商编号查询商品库
     * @param providerId
     * @return
     */
    @GetMapping("/findByProviderId")
    @ResponseBody
    public ResultVO findByProviderId(@RequestParam("providerId") Integer providerId,
                                     @RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (providerId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        PageRequest pageRequest = PageRequest.of(page -1, size);
        Page<ProviderProductRelatVo> providerProductRelatVoPage = providerProductRelatService.findByProviderId(providerId, pageRequest);

        Map<String, Object> map = new HashMap<>();
        map.put("data", providerProductRelatVoPage.getContent());
        map.put("totalPage", providerProductRelatVoPage.getTotalPages());
        return ResultVOUtil.success(map);
    }

    /**
     * 供应商管理->进入商品库->新增
     * @param providerProductRelatForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/repoAdd")
    public ResultVO repoAdd(@Valid ProviderProductRelatForm providerProductRelatForm,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【新增商品】providerProductRelatForm = {}", providerProductRelatForm);
            throw new GlobalException(ResultEnum.ADD_PRODUCT_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        providerProductRelatService.save(providerProductRelatForm);
        // 记录供应商商品的采购价日志
        ProviderProductLog providerProductLog = new ProviderProductLog();
        providerProductLog.setProviderId(providerProductRelatForm.getProviderId());
        providerProductLog.setProductId(providerProductRelatForm.getProductId());
        providerProductLog.setPurchasePrice(providerProductRelatForm.getPurchasePrice());
        providerProductLog.setProductStock(providerProductRelatForm.getProductStock());
        providerProductLogService.save(providerProductLog);
        return ResultVOUtil.success();
    }

    /**
     * 供应商管理->进入商品库->修改
     * @param providerProductRelatForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/repoUpdate")
    public ResultVO repoUpdate(@Valid ProviderProductRelatForm providerProductRelatForm,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【更新商品】providerProductRelatForm = {}", providerProductRelatForm);
            throw new GlobalException(ResultEnum.UPDATE_PRODUCT_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        providerProductRelatService.update(providerProductRelatForm);
        // 记录供应商商品的采购价日志
        ProviderProductLog providerProductLog = new ProviderProductLog();
        providerProductLog.setProviderId(providerProductRelatForm.getProviderId());
        providerProductLog.setProductId(providerProductRelatForm.getProductId());
        providerProductLog.setPurchasePrice(providerProductRelatForm.getPurchasePrice());
        providerProductLog.setProductStock(providerProductRelatForm.getProductStock());
        providerProductLogService.save(providerProductLog);
        return ResultVOUtil.success();
    }

    /**
     * 查询某个供应商的某个商品的历史采购价记录列表
     * @param providerId 供应商id
     * @param productId 商品id
     * @return 个供应商的某个商品的历史采购价记录列表
     */
    @GetMapping("/findPurchasePriceHistory")
    public ResultVO findPurchasePriceHistory(@RequestParam("providerId") Integer providerId,
                                             @RequestParam("productId") Integer productId) {
        List<ProviderProductLog> historyList = providerProductLogService.findPurchasePriceHistory(providerId, productId);
        return ResultVOUtil.success(historyList);
    }

    @GetMapping("/repoDel")
    public ResultVO repoDel(@RequestParam("id") Integer id) {
        if (id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        providerProductRelatService.delete(id);
        return ResultVOUtil.success();
    }

    @GetMapping("/findRepoById")
    public ResultVO findRepoById(@RequestParam("productId") Integer productId,
                                 @RequestParam("providerId") Integer providerId) {
        if (productId == null || providerId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        ProviderProductRelat result = providerProductRelatService.findByProviderIdAndProductId(productId, providerId);
        return ResultVOUtil.success(result);

    }

    @GetMapping("/findProviderByProductId")
    public ResultVO findProviderByProductId(@RequestParam("productId") Integer productId) {
        if (productId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<ProviderProductRelatVo> providerProductRelatVoList = providerProductRelatService.findProviderByProductId(productId);
//        providerProductRelatService.find
        return ResultVOUtil.success(providerProductRelatVoList);
    }

    @GetMapping("/getProductByJanOrName")
    public ResultVO getProductByJanOrName(@RequestParam("param") String param) {
        if (StringUtils.isEmpty(param)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<ProviderProductRelat> providerProductRelatList = providerProductRelatService.getProductByJanOrName(param);
        return ResultVOUtil.success(providerProductRelatList);
    }

    // 需要只查询当前供应商的商品
    @GetMapping("/getProviderProductByJanOrName")
    public ResultVO getProviderProductByJanOrName(@RequestParam("providerId") Integer providerId, @RequestParam("param") String param) {
        if (providerId == null || StringUtils.isEmpty(param)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<ProviderProductRelat> providerProductRelatList = providerProductRelatService.getProductByJanOrName(providerId, param);
        return ResultVOUtil.success(providerProductRelatList);
    }

    @GetMapping("/getCurrency")
    public ResultVO getCurrency(@RequestParam("providerId") Integer providerId) {
        if (providerId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        ProductProvider productProvider = productProviderService.findOne(providerId);
        if (productProvider == null) {
            throw new GlobalException(ResultEnum.PROVIDER_NOT_EXIST);
        }
        return ResultVOUtil.success(productProvider.getCurrencyEnum().getCurrency());
    }
}
