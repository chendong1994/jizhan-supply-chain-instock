package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.VO.ShopDetailVO;
import com.jizhangyl.application.VO.ShopVO;
import com.jizhangyl.application.converter.Shop2ShopVOConverter;
import com.jizhangyl.application.dataobject.primary.Brand;
import com.jizhangyl.application.dataobject.primary.Cate;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.primary.Shop;
import com.jizhangyl.application.dto.ShopDto;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.enums.ShopStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.OrderDetailRepository;
import com.jizhangyl.application.repository.primary.OrderMasterRepository;
import com.jizhangyl.application.repository.primary.ShopRepository;
import com.jizhangyl.application.service.BrandService;
import com.jizhangyl.application.service.CateService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/1 16:10
 * @description
 */
@Slf4j
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopRepository repository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private CateService cateService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BrandService brandService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /*@Override
    public Page<ShopVO> findAll(Pageable pageable) {
        Page<Shop> shopPage = repository.findAll(pageable);
        List<ShopVO> shopVOList = Shop2ShopVOConverter.convert(shopPage.getContent());
        return new PageImpl<>(shopVOList, pageable, shopPage.getTotalElements());
    }*/

    @Override
    public Page<Shop> findAll(Pageable pageable) {
        Page<Shop> shopPage = repository.findAll(pageable);
        return shopPage;
    }

    /*@Override
    public Page<ShopVO> findUp(Pageable pageable) {
        Page<Shop> shopPage = repository.findByShopStatus(ShopStatusEnum.UP.getCode(), pageable);
        List<ShopVO> shopVOList = Shop2ShopVOConverter.convert(shopPage.getContent());
        return new PageImpl<>(shopVOList, pageable, shopPage.getTotalElements());
    }*/

    @Override
    public Page<Shop> findUp(Pageable pageable) {
        Page<Shop> shopPage = repository.findByShopStatus(ShopStatusEnum.UP.getCode(), pageable);
        return shopPage;
    }

    @Override
    public List<ShopVO> findByCateId(Integer cateId) {
        if (cateId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<Shop> shopList = repository.findByCateId(cateId);
        List<ShopVO> shopVOList = Shop2ShopVOConverter.convert(shopList);
        return shopVOList;
    }

    /**
     * 查询某类目下所有上架物品
     * @param cateId
     * @param pageable
     * @return
     */
    @Override
    public Page<ShopVO> findByCateId(Integer cateId, Pageable pageable) {
        Page<Shop> shopPage = repository.findByCateIdAndShopStatus(cateId, ShopStatusEnum.UP.getCode(), pageable);
        List<ShopVO> shopVOList = Shop2ShopVOConverter.convert(shopPage.getContent());
        return new PageImpl<>(shopVOList, pageable, shopPage.getTotalElements());
    }

    @Override
    public ShopDetailVO detail(Integer shopId) {
        if (shopId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        Shop shop = repository.getOne(shopId);
        if (shop == null) {
            throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        ShopDetailVO shopDetailVO = new ShopDetailVO();
        BeanUtils.copyProperties(shop, shopDetailVO);

        shopDetailVO.setCateName(cateService.findOne(shop.getCateId()).getName());
        shopDetailVO.setBrandName(brandService.findOne(shop.getBrandId()).getName());
        return shopDetailVO;

    }

    @Override
    public Shop findOne(Integer productId) {
        if (productId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return repository.getOne(productId);
    }

    @Transactional
    @Override
    public Shop save(Shop shop) {
        try {
            if (shop == null) {
                throw new GlobalException(ResultEnum.PRODUCT_ADD_FAIL);
            }
            Shop result = repository.save(shop);
            if (result == null) {
                throw new GlobalException(ResultEnum.PRODUCT_ADD_FAIL);
            }
            return result;
        } catch (Exception e) {
            log.error("【商品新增】shop = {}", shop);
            throw new GlobalException(ResultEnum.PRODUCT_ADD_FAIL);
        }
    }

    @Override
    public List<ShopDto> findAll() {
        List<Shop> shopList = repository.findAll();
        if (CollectionUtils.isEmpty(shopList)) {
            throw new GlobalException(ResultEnum.PRODUCT_REPOSITORY_EMPTY);
        }

        List<Integer> cateIdList = shopList.stream().map(e -> e.getCateId()).collect(Collectors.toList());
        List<Integer> brandIdList = shopList.stream().map(e -> e.getBrandId()).collect(Collectors.toList());

        List<Cate> cateList = cateService.findByIdIn(cateIdList);
        List<Brand> brandList = brandService.findByIdIn(brandIdList);


        List<ShopDto> shopDtoList = shopList.stream().map(e -> {
            ShopDto shopDto = new ShopDto();
            BeanUtils.copyProperties(e, shopDto);

            for (Cate cate : cateList) {
                if (cate.getId().equals(e.getCateId())) {
                    shopDto.setCateName(cate.getName());
                }
            }
            for (Brand brand : brandList) {
                if (brand.getId().equals(e.getBrandId())) {
                    shopDto.setBrandName(brand.getName());
                }
            }
            shopDto.setShopStatus(e.getShopStatusEnum().getMsg());
            return shopDto;
        }).collect(Collectors.toList());

        return shopDtoList;
    }

    @Override
    public Shop findByShopJan(String shopJan) {
        if (StringUtils.isEmpty(shopJan)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return repository.findByShopJan(shopJan);
    }

    /**
     * 加库存
     * @param orderDetailList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void increaseStock(List<OrderDetail> orderDetailList) {
        for (OrderDetail orderDetail : orderDetailList) {
            Shop shop = repository.getOne(Integer.valueOf(orderDetail.getProductId()));
            if (shop == null) {
                throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer result = shop.getShopCount() + orderDetail.getProductQuantity();
            shop.setShopCount(result);
            repository.save(shop);
        }
    }

    /**
     * 减库存
     * @param orderDetailList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decreaseStock(List<OrderDetail> orderDetailList) {
        for (OrderDetail orderDetail : orderDetailList) {
            Shop shop = repository.getOne(Integer.valueOf(orderDetail.getProductId()));
            if (shop == null) {
                throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            Integer result = shop.getShopCount() - orderDetail.getProductQuantity();
            if (result < 0) {
                log.error("【扣库存】错误, Jancode = {}", shop.getShopJan());
                throw new GlobalException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH.getCode(), "商品库存不足, Jancode = " + shop.getShopJan());
            }
            shop.setShopCount(result);
            repository.save(shop);
        }
    }


    /**
     * shopDtoList 批量入库
     * @param shopDtoList
     * @return
     */
    @Transactional
    @Override
    public List<Shop> saveInBatch(List<ShopDto> shopDtoList) {
        if (CollectionUtils.isEmpty(shopDtoList)) {
            throw new GlobalException(ResultEnum.PRODUCT_IMPORT_ERROR.getCode(), String.format(ResultEnum.PRODUCT_IMPORT_ERROR.getMessage(), "导入列表为空"));
        }
        List<Shop> shopList = shopDtoList.stream().map(e -> {
            Shop shop = new Shop();
            BeanUtils.copyProperties(e, shop);
            Cate cate = cateService.findByName(e.getCateName());
            Brand brand = brandService.findByName(e.getBrandName());
/*            if (cate == null) {
                throw new GlobalException(ResultEnum.PRODUCT_IMPORT_ERROR.getCode(), String.format(ResultEnum.PRODUCT_IMPORT_ERROR.getMessage(), "Jancode = " + e.getShopJan() + "的商品对应的类别不存在"));
            }
            if (brand == null) {
                throw new GlobalException(ResultEnum.PRODUCT_IMPORT_ERROR.getCode(), String.format(ResultEnum.PRODUCT_IMPORT_ERROR.getMessage(), "Jancode = " + e.getShopJan() + "的商品对应的品牌不存在"));
            }*/
            if (cate != null) {
                shop.setCateId(cate.getId());
            }
            if (brand != null) {
                shop.setBrandId(brand.getId());
            }
            return shop;
        }).collect(Collectors.toList());
        List<Shop> saveResult = repository.saveAll(shopList);

        return saveResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Integer id) {
        try {
            Shop shop = findOne(id);
            if (shop == null) {
                throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            String shopImageUrl = shop.getShopImage();

            // 删除记录
            repository.deleteById(id);

            // 删除 oss 旧图
            if (!StringUtils.isEmpty(shopImageUrl)) {
                String fileName = shopImageUrl.substring(shopImageUrl.lastIndexOf("/") + 1, shopImageUrl.length());
                fileUploadUtil.delete(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResultEnum.PRODUCT_DELETE_FAIL);
        }
    }

    /**
     * 商品上架
     * @param productId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void up(Integer productId) {
        Shop shop = repository.getOne(productId);
        if (shop == null) {
            throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        Integer status = shop.getShopStatus();
        if (status == null || status.equals(ShopStatusEnum.UP.getCode())) {
            throw new GlobalException(ResultEnum.PRODUCT_STATUS_IS_UP);
        }
        shop.setShopStatus(ShopStatusEnum.UP.getCode());
        Shop result = repository.save(shop);
        if (result == null) {
            throw new GlobalException(ResultEnum.PRODUCT_STATUS_UPDATE_FAIL);
        }
    }

    /**
     * 商品下架
     * @param productId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void down(Integer productId) {
        Shop shop = repository.getOne(productId);
        if (shop == null) {
            throw new GlobalException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        Integer status = shop.getShopStatus();
        if (status == null || status.equals(ShopStatusEnum.DOWN.getCode())) {
            throw new GlobalException(ResultEnum.PRODUCT_STATUS_IS_DOWN);
        }
        shop.setShopStatus(ShopStatusEnum.DOWN.getCode());
        Shop result = repository.save(shop);
        if (result == null) {
            throw new GlobalException(ResultEnum.PRODUCT_STATUS_UPDATE_FAIL);
        }
    }

    @Override
    public List<Shop> findByName(String name) {
        return repository.findByShopNameLike("%" + name + "%");
    }

    // <=========修复冲突========>
    @Override
    public List<Shop> findByIdIn(List<Integer> idList) {
        return repository.findByIdIn(idList);
    }

    @Override
    public Shop findByPackCode(String packCode) {
        return repository.findByPackCode(packCode);
    }

    @Override
    public List<Shop> findByCriteria(String param) {
        return repository.findByCriteria(param);
    }

    @Override
    public void productDataSync() {
        // TODO 将更新的商品数据同步至其他表
//        repository.syncDataToOthers();

    }

    @Override
    public List<Shop> getAll() {
        return repository.findAll();
    }

    @Override
    public int findShopSalesNumBetween(Date beginDate, Date endDate, String productId) {
        // 1.从订单明细中找出指定时间段内的该商品的所有订单
        List<OrderDetail> detailList = orderDetailRepository.findByProductIdAndCreateTimeBetween(productId, beginDate, endDate);
        
        // 2.只筛选出已经付款的订单
        List<String> orderIds = detailList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
        List<OrderMaster> masterList = orderMasterRepository.findByOrderIdIn(orderIds);
        masterList = masterList.stream()
                               .filter(e -> e.getOrderStatus().equals(OrderStatusEnum.PAID.getCode()))
                               .collect(Collectors.toList());
        
        for (ListIterator<OrderDetail> it = detailList.listIterator(); it.hasNext(); ) {
            OrderDetail orderDetail = it.next();
            boolean isExist = false;
            for (OrderMaster master : masterList) {
                if (master.getOrderId().equals(orderDetail.getOrderId())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                it.remove();
            }
        }
        // 3.计算出该商品的销量
        int salesNum = detailList.stream()
                                 .map(e -> e.getProductQuantity())
                                 .reduce(0, (e1, e2) -> e1 + e2);
        return salesNum;
    }
}
