package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ProductImageVO;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.VO.ShopDetailVO;
import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.dataobject.primary.Brand;
import com.jizhangyl.application.dataobject.primary.Cate;
import com.jizhangyl.application.dataobject.primary.RepositoryProduct;
import com.jizhangyl.application.dataobject.primary.Shop;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.enums.ShopStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.ShopForm;
import com.jizhangyl.application.service.BrandService;
import com.jizhangyl.application.service.CateService;
import com.jizhangyl.application.service.ProductImageService;
import com.jizhangyl.application.service.ProductReportService;
import com.jizhangyl.application.service.RepositoryProductService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.FileUploadUtil;
import com.jizhangyl.application.utils.ProductImageVOUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:27
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CateService cateService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private ProductReportService productReportService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private RepositoryProductService repositoryProductService;

    @GetMapping("/list")
    public ResultVO list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                         @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        // 1. 查询所有上架商品
        Page<Shop> shopPage = shopService.findAll(pageRequest);
        List<Shop> shopList = shopPage.getContent();

        // 2. 查询类目和品牌（一次性查询）
        List<Integer> cateIdList = shopList.stream().map(e -> e.getCateId()).collect(Collectors.toList());
        List<Integer> brandIdList = shopList.stream().map(e -> e.getBrandId()).collect(Collectors.toList());

        List<Cate> cateList = cateService.findByIdIn(cateIdList);
        List<Brand> brandList = brandService.findByIdIn(brandIdList);


        // 3. 数据拼装
        List<ShopVO> shopVOList = new ArrayList<>();
        for (Shop shop : shopList) {
            ShopVO shopVO = new ShopVO();
            BeanUtils.copyProperties(shop, shopVO);

            for (Cate cate : cateList) {
                if (cate.getId().equals(shop.getCateId())) {
                    shopVO.setCateName(cate.getName());
                }
            }
            for (Brand brand : brandList) {
                if (brand.getId().equals(shop.getBrandId())) {
                    shopVO.setBrandName(brand.getName());
                }
            }

            // 昨日销量
            int yestSalesNum = shopService.findShopSalesNumBetween(DateUtil.getStart(-1), DateUtil.getStart(0), String.valueOf(shop.getId()));
            shopVO.setYestSalesNum(BigDecimal.valueOf(yestSalesNum));

            // 过去7天销量
            int lastWeekSalesNum = shopService.findShopSalesNumBetween(DateUtil.getStart(-7), DateUtil.getStart(0), String.valueOf(shop.getId()));
            shopVO.setLastWeekSalesNum(BigDecimal.valueOf(lastWeekSalesNum).divide(BigDecimal.valueOf(7), 2, BigDecimal.ROUND_HALF_UP));

            // 过去30天销量
            int lastMonthSalesNum = shopService.findShopSalesNumBetween(DateUtil.getStart(-30), DateUtil.getStart(0), String.valueOf(shop.getId()));
            shopVO.setLastMonthSalesNum(BigDecimal.valueOf(lastMonthSalesNum).divide(BigDecimal.valueOf(30), 2, BigDecimal.ROUND_HALF_UP));

            shopVOList.add(shopVO);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("goodList", shopVOList);
        result.put("totalpage", shopPage.getTotalPages());
        return ResultVOUtil.success(result);
    }

    @GetMapping("/findByCateId")
    // 使用缓存需解决商品更新，缓存同步更新或让该部分失效
//    @Cacheable(cacheNames = "product", key = "#cateId", condition = "#cateId > 0", unless = "#result.getCode() != 0")
    public ResultVO findById(@RequestParam("id") Integer cateId,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (cateId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ShopVO> shopVOPage = shopService.findByCateId(cateId, pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("shopList", shopVOPage.getContent());
        result.put("totalpage", shopVOPage.getTotalPages());
        return ResultVOUtil.success(result);
    }

    @GetMapping("/detail")
    public ResultVO detail(@RequestParam("id") Integer shopId) {
        if (shopId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        ShopDetailVO shopDetailVO = shopService.detail(shopId);
        return ResultVOUtil.success(shopDetailVO);

    }

    /**
     * 商品导出
     * @return
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {
        return productReportService.exportProductList2Excel(shopService.findAll());
    }

    @ResponseBody
    @PostMapping("/import")
    public ResultVO importShop(@RequestParam("myfile") MultipartFile file) {
        List<Shop> shopList = productReportService.importShop2List(file);
        return ResultVOUtil.success();
    }

    /**
     * 查询所有上架商品
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/findUp")
//    @Cacheable(cacheNames = "product", key = "123")
    public ResultVO findUp(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                           @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Shop> shopPage = shopService.findUp(pageRequest);

        List<ShopVO> shopVOList = shopPage.getContent().stream().map(e -> {
            ShopVO shopVO = new ShopVO();
            BeanUtils.copyProperties(e, shopVO);
            return shopVO;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("goodList", shopVOList);
        result.put("totalpage", shopPage.getTotalPages());
        return ResultVOUtil.success(result);
    }

    /**
     * 商品图片上传
     *
     * @param file
     * @return
     */
    @ResponseBody
    @PostMapping("/imageUpload")
    public ResultVO imageUpload(@RequestParam("productId") Integer productId,
                                @RequestParam("productImage") MultipartFile file) {
        if (productId == null) {
            log.error("【商品图片上传】productId = {}", productId);
            throw new GlobalException(ResultEnum.PARAM_ERROR);
        }

        Shop shop = shopService.findOne(productId);
        if (shop == null) {
            log.error("【商品图片上传】shop = {}", shop);
            throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
        }

        // 得到该图片外网访问地址
        String outsideUrl = fileUploadUtil.upload(file);
        shop.setShopImage(outsideUrl);
        shopService.save(shop);

        log.info("【商品图片上传】上传成功");
        return ResultVOUtil.success();
    }

    /**
     * 新增商品
     * @param shopForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/save")
//    @CachePut(cacheNames = "product", key = "123")
//    @CacheEvict(cacheNames = "product", key = "123")
    @Transactional(rollbackFor = Exception.class)
    public ResultVO save(@Valid ShopForm shopForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        if (!StringUtils.isEmpty(shopForm.getShopJan())) {
            Shop shop = shopService.findByShopJan(shopForm.getShopJan());
            if (shop != null) {
                throw new GlobalException(ResultEnum.JANCODE_IS_EXIST);
            }
        }
        if (!StringUtils.isEmpty(shopForm.getPackCode())) {
            Shop shop = shopService.findByPackCode(shopForm.getPackCode());
            if (shop != null) {
                throw new GlobalException(ResultEnum.PACK_CODE_IS_EXIST);
            }
        }

        String shopImageUrl = shopForm.getShopImage();

        try {
            Shop shop = new Shop();
            BeanUtils.copyProperties(shopForm, shop);

            // 供货价
            BigDecimal gPrice = shop.getShopGprice();
            // 默认下架
            shop.setShopStatus(ShopStatusEnum.DOWN.getCode());

            // 设置是否抛货
            String shopWhg = shopForm.getShopWhg();
            String[] whg = shopWhg.split("x");
            Integer multiResult = (Integer.parseInt(whg[0]) * Integer.parseInt(whg[1]) * Integer.parseInt(whg[2])) / 6000;
            // 0 - 不是抛货, 1 - 抛货
            Integer isPaogoods = multiResult > shopForm.getShopJweight() ? 1 : 0;
            // 设置是否抛货
            shop.setIsPaogoods(isPaogoods);

            Shop result = shopService.save(shop);

            // 同步至仓库商品表
            RepositoryProduct repositoryProduct = new RepositoryProduct();
            repositoryProduct.setProductId(result.getId());
            repositoryProduct.setProductName(result.getShopName());
            repositoryProduct.setProductStock(0);
            repositoryProduct.setWayStock(0);
            repositoryProduct.setYestOutNum(0);
            repositoryProduct.setWeekOutNum(0);
            repositoryProduct.setTodoOutNum(0);
            repositoryProduct.setProductImage(result.getShopImage());
            repositoryProduct.setProductJancode(result.getShopJan());
            repositoryProduct.setBoxNum(result.getShopXcount());
            repositoryProduct.setPackCode(result.getPackCode());
            repositoryProductService.save(repositoryProduct);

        } catch (Exception e) {
            // 新增商品失败删除对应 oss 文件
            if (!StringUtils.isEmpty(shopImageUrl)) {
                String fileName = shopImageUrl.substring(shopImageUrl.lastIndexOf("/") + 1);
                fileUploadUtil.delete(fileName);
            }
            throw new GlobalException(ResultEnum.PRODUCT_ADD_FAIL);
        }
        return ResultVOUtil.success();
    }

    @PostMapping("/update")
//    @CachePut(cacheNames = "product", key = "123")
//    @CacheEvict(cacheNames = "product", key = "123")
    @Transactional(rollbackFor = Exception.class)
    public ResultVO update(@Valid ShopForm shopForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        if (shopForm.getId() == null) {
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(), "商品id为空"));
        }

        if (!StringUtils.isEmpty(shopForm.getShopJan())) {
            Shop shop = shopService.findByShopJan(shopForm.getShopJan());
            if (shop != null && !shop.getId().equals(shopForm.getId())) {
                throw new GlobalException(ResultEnum.JANCODE_IS_EXIST);
            }
        }
        if (!StringUtils.isEmpty(shopForm.getPackCode())) {
            Shop shop = shopService.findByPackCode(shopForm.getPackCode());
            if (shop != null && !shop.getId().equals(shopForm.getId())) {
                throw new GlobalException(ResultEnum.PACK_CODE_IS_EXIST);
            }
        }

        try {
            Shop shop = shopService.findOne(shopForm.getId());
            Integer shopStatus = shop.getShopStatus();
            if (shop == null) {
                throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            String oldImageUrl = null;

            if (!StringUtils.isEmpty(shop.getShopImage()) &&
                    !StringUtils.isEmpty(shopForm.getShopImage()) &&
                    !shop.getShopImage().equals(shopForm.getShopImage())) {
                oldImageUrl = shop.getShopImage();
            }

            if (StringUtils.isEmpty(shopForm.getShopImage()) && !StringUtils.isEmpty(oldImageUrl)) {
                shopForm.setShopImage(oldImageUrl);
            }
            // form 中带有 id, 直接 copy
            BeanUtils.copyProperties(shopForm, shop);

            // 供货价
            BigDecimal shopGprice = shop.getShopGprice();

            // 设置上下架状态为修改前状态
            shop.setShopStatus(shopStatus);

            // 设置是否抛货
            String shopWhg = shopForm.getShopWhg();
            String[] whg = shopWhg.split("x");
            Integer multiResult = (Integer.parseInt(whg[0]) * Integer.parseInt(whg[1]) * Integer.parseInt(whg[2])) / 6000;
            // 0 - 不是抛货, 1 - 抛货
            Integer isPaogoods = multiResult > shopForm.getShopJweight() ? 1 : 0;
            // 设置是否抛货
            shop.setIsPaogoods(isPaogoods);

            // 保存
            Shop result = shopService.save(shop);

            // 对旧的图片地址进行处理
            if (oldImageUrl == null) {
                // 删除 oss 旧图
                if (!StringUtils.isEmpty(oldImageUrl)) {
                    String fileName = oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1);
                    fileUploadUtil.delete(fileName);
                }
            }


            // 同步至仓库商品表
            RepositoryProduct repositoryProduct = repositoryProductService.findByProductJancode(shop.getShopJan());
            if (repositoryProduct != null) {
                repositoryProduct.setProductId(result.getId());
                repositoryProduct.setProductName(result.getShopName());
                repositoryProduct.setProductImage(result.getShopImage());
                repositoryProduct.setProductJancode(result.getShopJan());
                repositoryProduct.setBoxNum(result.getShopXcount());
                repositoryProduct.setPackCode(result.getPackCode());
                repositoryProductService.save(repositoryProduct);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResultEnum.PRODUCT_UPDATE_FAIL);
        }
        return ResultVOUtil.success();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
//    @CacheEvict(cacheNames = "product", key = "123")
    public ResultVO delete(@RequestParam("id") Integer id) {
        if (id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        shopService.delete(id);
        return ResultVOUtil.success();
    }

    /**
     * 商品上架
     */
    @GetMapping("/up")
    public ResultVO up(@RequestParam("productId") Integer productId) {
        if (productId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        shopService.up(productId);
        return ResultVOUtil.success();
    }

    /**
     * 商品下架
     */
    @GetMapping("/down")
    public ResultVO down(@RequestParam("productId") Integer productId) {
        if (productId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        shopService.down(productId);
        return ResultVOUtil.success();
    }

    /**
     * 按照商品名称模糊查询
     * @param name
     * @return
     */
    @GetMapping("/findByName")
    public ResultVO findByName(@RequestParam("name") String name) {
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        List<Shop> shopList = new ArrayList<>();

        if (StringUtils.isNumeric(name)) {
            Shop shop = shopService.findByShopJan(StringUtils.trimToNull(name));
            if (shop != null) {
                shopList.add(shop);
            } else {
                shopList.addAll(shopService.findByName(name));
            }
        } else {
            shopList.addAll(shopService.findByName(name));
        }

        List<ShopVO> shopVOList = shopList.stream().map(e -> {
            ShopVO shopVO = new ShopVO();
            BeanUtils.copyProperties(e, shopVO);
            return shopVO;
        }).collect(Collectors.toList());

        // 筛选掉下架的商品
        shopVOList = shopVOList.stream().filter(e -> {
            if (e.getShopStatus().equals(ShopStatusEnum.UP.getCode())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        return ResultVOUtil.success(shopVOList);
    }


    /**
     * 按照商品名称模糊查询(PC)
     * @param name
     * @return
     */
    @GetMapping("/getByName")
    public ResultVO getByName(@RequestParam("name") String name) {
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        List<Shop> shopList = new ArrayList<>();

        if (StringUtils.isNumeric(name)) {
            Shop shop = shopService.findByShopJan(StringUtils.trimToNull(name));
            if (shop != null) {
                shopList.add(shop);
            } else {
                shopList.addAll(shopService.findByName(name));
            }
        } else {
            shopList.addAll(shopService.findByName(name));
        }

        return ResultVOUtil.success(shopList);
    }

    /**
     * 按照jan_code, 商品名称, 海关商品唯一码, 仓库打包识别码
     * @param param
     * @return
     */
    @GetMapping("/findByCriteria")
    public ResultVO findByCriteria(@RequestParam("param") String param) {
        if (org.springframework.util.StringUtils.isEmpty(param)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        List<Shop> shopList = shopService.findByCriteria(param);

        List<Integer> categoryIdList = shopList.stream().map(e -> e.getCateId()).collect(Collectors.toList());
        List<Integer> brandIdList = shopList.stream().map(e -> e.getBrandId()).collect(Collectors.toList());

        List<Cate> cateList = cateService.findByIdIn(categoryIdList);
        List<Brand> brandList = brandService.findByIdIn(brandIdList);

        List<ShopVO> shopVOList = new ArrayList<>();
        for (Shop shop : shopList) {
            ShopVO shopVO = new ShopVO();
            BeanUtils.copyProperties(shop, shopVO);

            for (Cate cate : cateList) {
                if (cate.getId().equals(shop.getCateId())) {
                    shopVO.setCateName(cate.getName());
                    break;
                }
            }

            for (Brand brand : brandList) {
                if (brand.getId().equals(shop.getBrandId())) {
                    shopVO.setBrandName(brand.getName());
                    break;
                }
            }

            // 昨日销量
            int yestSalesNum = shopService.findShopSalesNumBetween(DateUtil.getStart(-1), DateUtil.getStart(0), String.valueOf(shop.getId()));
            shopVO.setYestSalesNum(BigDecimal.valueOf(yestSalesNum));

            // 过去7天销量
            int lastWeekSalesNum = shopService.findShopSalesNumBetween(DateUtil.getStart(-7), DateUtil.getStart(0), String.valueOf(shop.getId()));
            shopVO.setLastWeekSalesNum(BigDecimal.valueOf(lastWeekSalesNum).divide(BigDecimal.valueOf(7), 2, BigDecimal.ROUND_HALF_UP));

            // 过去30天销量
            int lastMonthSalesNum = shopService.findShopSalesNumBetween(DateUtil.getStart(-30), DateUtil.getStart(0), String.valueOf(shop.getId()));
            shopVO.setLastMonthSalesNum(BigDecimal.valueOf(lastMonthSalesNum).divide(BigDecimal.valueOf(30), 2, BigDecimal.ROUND_HALF_UP));

            shopVOList.add(shopVO);
        }

        return ResultVOUtil.success(shopVOList);
    }

    /**
     * 商品图片上传
     */
    @PostMapping("/detailImageUpload")
    public ResultVO detailImageUpload(@RequestParam("productId") Integer productId,
                                @RequestParam("productImages") String productImages) {
        if (productId == null || StringUtils.isEmpty(productImages)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        List<ProductImageVO> productImageVOList = ProductImageVOUtil.convert(productImages);

        productImageService.save(productId, productImageVOList);

        return ResultVOUtil.success();
    }

    @GetMapping("/imageList")
    public ResultVO imageList(@RequestParam("productId") Integer productId) {
        if (productId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<ProductImageVO> productImageVOList = productImageService.imageList(productId);

        return ResultVOUtil.success(productImageVOList);
    }
}