package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.converter.PurchaseOrderMaster2PurchaseOrderDtoConverter;
import com.jizhangyl.application.dataobject.ConfirmCert;
import com.jizhangyl.application.dataobject.OrderDetail;
import com.jizhangyl.application.dataobject.PayCert;
import com.jizhangyl.application.dataobject.ProductProvider;
import com.jizhangyl.application.dataobject.ProviderProductRelat;
import com.jizhangyl.application.dataobject.PurchaseConfirmDetail;
import com.jizhangyl.application.dataobject.PurchaseOrderConfirm;
import com.jizhangyl.application.dataobject.PurchaseOrderDetail;
import com.jizhangyl.application.dataobject.PurchaseOrderMaster;
import com.jizhangyl.application.dataobject.PurchaseOrderPay;
import com.jizhangyl.application.dataobject.PurchasePayDetail;
import com.jizhangyl.application.dataobject.Shop;
import com.jizhangyl.application.dataobject.VerifyCert;
import com.jizhangyl.application.dto.PurchaseOrderDto;
import com.jizhangyl.application.enums.PurchaseOrderStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.PurchaseConfirmDetailRepository;
import com.jizhangyl.application.repository.PurchaseOrderConfirmRepository;
import com.jizhangyl.application.repository.PurchaseOrderDetailRepository;
import com.jizhangyl.application.repository.PurchaseOrderMasterRepository;
import com.jizhangyl.application.repository.PurchaseOrderPayRepository;
import com.jizhangyl.application.repository.PurchasePayDetailRepository;
import com.jizhangyl.application.service.ConfirmCertService;
import com.jizhangyl.application.service.PayCertService;
import com.jizhangyl.application.service.ProductProviderService;
import com.jizhangyl.application.service.ProviderProductRelatService;
import com.jizhangyl.application.service.PurchaseOrderCurrencyService;
import com.jizhangyl.application.service.PurchaseOrderService;
import com.jizhangyl.application.service.RepositoryProductService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.service.VerifyCertService;
import com.jizhangyl.application.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/1 9:57
 * @description
 */
@Slf4j
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private VerifyCertService verifyCertService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RepositoryProductService repositoryProductService;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private PurchaseConfirmDetailRepository purchaseConfirmDetailRepository;

    @Autowired
    private PurchasePayDetailRepository purchasePayDetailRepository;

    @Autowired
    private PurchaseOrderMasterRepository purchaseOrderMasterRepository;

    @Autowired
    private ProductProviderService productProviderService;

    @Autowired
    private ProviderProductRelatService providerProductRelatService;

    @Autowired
    private PurchaseOrderConfirmRepository purchaseOrderConfirmRepository;

    @Autowired
    private PurchaseOrderPayRepository purchaseOrderPayRepository;

    @Autowired
    private ConfirmCertService confirmCertService;

    @Autowired
    private PayCertService payCertService;

    @Autowired
    private PurchaseOrderCurrencyService purchaseOrderCurrencyService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public PurchaseOrderDto update(PurchaseOrderDto purchaseOrderDto) {

        String orderId = purchaseOrderDto.getOrderId();
        PurchaseOrderMaster purchaseOrderMaster = findByOrderId(orderId);

        // 1. 存在性判断
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }

        // 2. 状态校验
        if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.NEW)) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_STATUS_ERROR);
        }

        // 3. 删除（detail）
        deletePurchaseOrderDetailByOrderId(orderId);
//        deletePurchaseOrder(orderId);

        // 4. 创建新的采购单
        PurchaseOrderDto result = create(purchaseOrderDto);

        return result;
    }

    /**
     * 新增采购订单
     *
     * @param purchaseOrderDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PurchaseOrderDto create(PurchaseOrderDto purchaseOrderDto) {
        if (CollectionUtils.isEmpty(purchaseOrderDto.getPurchaseOrderDetailList())) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_DETAIL_EMPTY);
        }

        String orderId = purchaseOrderDto.getOrderId();

        if (StringUtils.isEmpty(orderId)) {
            orderId = KeyUtil.genUniqueKey();
        }

        // 购物车所有商品总价
        BigDecimal orderAmount = BigDecimal.ZERO;
        PurchaseOrderMaster purchaseOrderMaster = new PurchaseOrderMaster();
        List<PurchaseOrderDetail> purchaseOrderDetailList = purchaseOrderDto.getPurchaseOrderDetailList();


        for (PurchaseOrderDetail detail : purchaseOrderDetailList) {
            Shop shop = shopService.findOne(detail.getProductId());
            if (shop == null) {
                throw new GlobalException(ResultEnum.PURCHASE_ORDER_PRODUCT_NOT_EXIST.getCode(), String.format(ResultEnum.PURCHASE_ORDER_PRODUCT_NOT_EXIST.getMessage(), detail.getProductId()));
            }
            detail.setDetailId(KeyUtil.genUniqueKey());
            detail.setOrderId(orderId);
            detail.setProductJancode(shop.getShopJan());
            detail.setProductName(shop.getShopName());
            detail.setProductImage(shop.getShopImage());

            // 查询出该供应商对该商品的报价
            ProviderProductRelat providerProductRelat = providerProductRelatService.findByProviderIdAndProductId(detail.getProductId(), purchaseOrderDto.getProviderId());
            if (providerProductRelat == null) {
                throw new GlobalException(ResultEnum.PROVIDER_PRODUCT_RELAT_ERROR);
            }
            BigDecimal supplyPrice = providerProductRelat.getPurchasePrice();
            // 设置供货商的报价，并非商品的价格
            detail.setProductPrice(supplyPrice);
            if (supplyPrice == null) {
                throw new GlobalException(ResultEnum.PROVIDER_PRODUCT_PRICE_EMPTY);
            }

            // 计算购物车该商品总价
            orderAmount = orderAmount.add(new BigDecimal(detail.getProductQuantity()).multiply(supplyPrice));

            // 采购订单详情入库
            purchaseOrderDetailRepository.save(detail);
        }

        /*// 商品加库存
        List<OrderDetail> orderDetailList = PurchaseOrderDetail2OrderDetailConverter.convert(purchaseOrderDetailList);
        shopService.increaseStock(orderDetailList);*/

        BeanUtils.copyProperties(purchaseOrderDto, purchaseOrderMaster);
        purchaseOrderMaster.setOrderId(orderId);
        purchaseOrderMaster.setOrderAmount(orderAmount);

        /*// 计算付款日期（下单日期 + 账期）
        Date payDate = calcPayDate(purchaseOrderDto.getLoanDate());
        purchaseOrderMaster.setPayDate(payDate);*/

        // 查询供应商名称
        ProductProvider productProvider = productProviderService.findOne(purchaseOrderDto.getProviderId());
        String providerName = productProvider.getCompanyName();

        purchaseOrderMaster.setProviderName(providerName);

        purchaseOrderMasterRepository.save(purchaseOrderMaster);

        PurchaseOrderDto resultDto = PurchaseOrderMaster2PurchaseOrderDtoConverter.convert(purchaseOrderMaster);

        return resultDto;
    }

    /**
     * 分页查询所有采购订单
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<PurchaseOrderMaster> findAll(Pageable pageable) {
        Page<PurchaseOrderMaster> purchaseOrderMasterPage = purchaseOrderMasterRepository.findAll(pageable);
        return purchaseOrderMasterPage;
    }

    @Override
    public PurchaseOrderMaster findByOrderId(String orderId) {
        return purchaseOrderMasterRepository.findByOrderId(orderId);
    }

    /**
     * 按照供应商名字模糊查询
     *
     * @param providerName
     * @param pageable
     * @return
     */
    @Override
    public Page<PurchaseOrderMaster> findByroviderName(String providerName, Pageable pageable) {
        Page<PurchaseOrderMaster> purchaseOrderMasterPage = purchaseOrderMasterRepository.findByProviderNameLike("%" + providerName + "%",
                pageable);
        return purchaseOrderMasterPage;
    }

    /**
     * 根据订单编号查询订单详情
     *
     * @param orderId
     * @return
     */
    @Override
    public List<PurchaseOrderDetail> findDetailByOrderId(String orderId) {
        List<PurchaseOrderDetail> purchaseOrderDetailList = purchaseOrderDetailRepository.findByOrderId(orderId);
        return purchaseOrderDetailList;
    }

    @Override
    public void cancel(String orderId) {
        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderMasterRepository.findOne(orderId);
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }
        if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.NEW.getCode())
                && !purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.CONFIRMED.getCode())) {
            throw new GlobalException(ResultEnum.ORDER_STATUS_ERROR);
        }
        purchaseOrderMaster.setOrderStatus(PurchaseOrderStatusEnum.CANCEL.getCode());
        purchaseOrderMasterRepository.save(purchaseOrderMaster);
    }

    /**
     * 付款修改状态
     *
     * @param orderId
     * @param realAmount
     */
    @Override
    public void paid(String orderId, String realAmount) {
        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderMasterRepository.findOne(orderId);
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }
        if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.NEW.getCode())) {
            throw new GlobalException(ResultEnum.ORDER_STATUS_ERROR);
        }
        purchaseOrderMaster.setOrderStatus(PurchaseOrderStatusEnum.PAID.getCode());
        purchaseOrderMasterRepository.save(purchaseOrderMaster);
    }

    public static Date calcPayDate(Integer loanDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, loanDate);
        return calendar.getTime();
    }

    public static void main(String[] args) throws Exception {
        Date date = calcPayDate(9876);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(date));
    }

    /**
     * 仓库确认采购订单
     *
     * @param purchaseOrderDto
     * @return
     */
    @Transactional
    @Override
    public PurchaseOrderDto confirm(PurchaseOrderDto purchaseOrderDto) {
        if (CollectionUtils.isEmpty(purchaseOrderDto.getPurchaseOrderDetailList())) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_DETAIL_EMPTY);
        }

        String orderId = purchaseOrderDto.getOrderId();
        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderMasterRepository.findOne(orderId);
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }

        // 如果是第一次入库则需设置payTime
        // 计算付款日期（下单日期 + 账期）
        // 添加状态判断
        if (purchaseOrderMaster.getPayDate() == null) {
            // 第一次入库判断状态
            if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.CONFIRMED.getCode())) {
                throw new GlobalException(ResultEnum.PURCHASE_ORDER_STATUS_ERROR);
            }
            // 设置账期日
            Date payDate = calcPayDate(purchaseOrderMaster.getLoanDate());
            purchaseOrderMaster.setPayDate(payDate);
            purchaseOrderMaster.setOrderStatus(PurchaseOrderStatusEnum.ACCESSED_PART.getCode());
            purchaseOrderMasterRepository.save(purchaseOrderMaster);
        } else {
            // 非第一次入库判断状态
            if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.ACCESSED_PART.getCode())) {
                throw new GlobalException(ResultEnum.PURCHASE_ORDER_STATUS_ERROR);
            }
        }

        // 采购订单 -> n次确认(PurchaseOrderConfirm) -> n*n个确认详情(PurchaseConfirmDetail)

        List<PurchaseConfirmDetail> purchaseConfirmDetailList = purchaseOrderDto
                .getPurchaseOrderDetailList().stream()
                .map(e -> {
                    PurchaseConfirmDetail purchaseConfirmDetail = new PurchaseConfirmDetail();
                    BeanUtils.copyProperties(e, purchaseConfirmDetail);
                    return purchaseConfirmDetail;
                }).collect(Collectors.toList());

        // 此次确认的商品总价
        BigDecimal enterAmount = BigDecimal.ZERO;
        String confirmId = KeyUtil.genUniqueKey();

        List<PurchaseConfirmDetail> saveList = new ArrayList<>();
        try {
            // 确认详情入库
            for (PurchaseConfirmDetail detail : purchaseConfirmDetailList) {
                Shop shop = shopService.findOne(detail.getProductId());
                detail.setDetailId(KeyUtil.genUniqueKey());
                detail.setConfirmId(confirmId);
                detail.setProductName(shop.getShopName());
                detail.setProductJancode(shop.getShopJan());
                detail.setProductImage(shop.getShopImage());

                // 查询出该供应商对该商品的报价
                ProviderProductRelat providerProductRelat = providerProductRelatService.findByProviderIdAndProductId(detail.getProductId(), purchaseOrderDto.getProviderId());
                if (providerProductRelat == null) {
                    throw new GlobalException(ResultEnum.PROVIDER_PRODUCT_RELAT_ERROR);
                }
                BigDecimal supplyPrice = providerProductRelat.getPurchasePrice();
                // 设置供货商的报价，并非商品的价格
                detail.setProductPrice(supplyPrice);
                if (supplyPrice == null) {
                    throw new GlobalException(ResultEnum.PROVIDER_PRODUCT_PRICE_EMPTY);
                }

                // 计算购物车该商品总价
                enterAmount = enterAmount.add(new BigDecimal(detail.getProductQuantity()).multiply(supplyPrice));

                // 采购订单详情入库
                saveList.add(detail);
            }

            // 确认详情批量入库
            purchaseConfirmDetailRepository.save(saveList);

            // 保存确认记录
            PurchaseOrderConfirm purchaseOrderConfirm = new PurchaseOrderConfirm();
            purchaseOrderConfirm.setConfirmId(confirmId);
            purchaseOrderConfirm.setOrderId(orderId);
            purchaseOrderConfirm.setEnterTime(new Date());
            purchaseOrderConfirm.setEnterAmount(enterAmount);

            // 确认记录入库
            purchaseOrderConfirmRepository.save(purchaseOrderConfirm);


            // 凭证入库
            List<String> certUrlList = purchaseOrderDto.getCertUrlList();
            if (!CollectionUtils.isEmpty(certUrlList)) {

                List<ConfirmCert> confirmCertList = new ArrayList<>();

                for (String certUrl : certUrlList) {
                    ConfirmCert confirmCert = new ConfirmCert();
                    confirmCert.setConfirmId(confirmId);
                    confirmCert.setConfirmCert(certUrl);
                    confirmCertList.add(confirmCert);
                }
                confirmCertService.save(confirmCertList);
            }

            // 加库存（小程序的商品库存和仓库商品的库存）
            List<OrderDetail> orderDetailList = saveList.stream().map(e -> {
                OrderDetail orderDetail = new OrderDetail();
                BeanUtils.copyProperties(e, orderDetail);
                orderDetail.setProductId(String.valueOf(e.getProductId()));
                return orderDetail;
            }).collect(Collectors.toList());

            // 小程序端商品加库存
            shopService.increaseStock(orderDetailList);

            List<PurchaseOrderDetail> purchaseOrderDetailList = saveList.stream().map(e -> {
                PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
                BeanUtils.copyProperties(e, purchaseOrderDetail);
                return purchaseOrderDetail;
            }).collect(Collectors.toList());

            // 仓库端加库存
            repositoryProductService.increaseProductStock(purchaseOrderDetailList);

            // 仓库端减在途库存
            repositoryProductService.decreaseWayStock(purchaseOrderDetailList);

        } catch (Exception e) {
            throw new GlobalException(ResultEnum.UPDATE_PURCHASE_ORDER_FAIL);
        }

        PurchaseOrderDto resultDto = PurchaseOrderMaster2PurchaseOrderDtoConverter.convert(purchaseOrderMaster);
        return resultDto;
    }

    /**
     * 按照订单编号分页查询订单详情
     *
     * @param orderId
     * @param pageable
     * @return
     */
    @Override
    public Page<PurchaseOrderDetail> findOrderById(String orderId, Pageable pageable) {
        Page<PurchaseOrderDetail> purchaseOrderDetailPage = purchaseOrderDetailRepository.findByOrderId(orderId, pageable);
        return purchaseOrderDetailPage;
    }

    @Override
    public List<PurchaseOrderMaster> findByOrderStatus(Integer orderStatus) {
        return purchaseOrderMasterRepository.findByOrderStatus(orderStatus);
    }

    @Override
    public List<PurchaseConfirmDetail> findAllConfirmDetail() {
        return purchaseConfirmDetailRepository.findAll();
    }

    @Override
    public List<PurchaseConfirmDetail> findConfirmDetailByConfirmIdIn(List<String> confirmIdList) {
        return purchaseConfirmDetailRepository.findByConfirmIdIn(confirmIdList);
    }

    @Override
    public List<PurchasePayDetail> findPayDetailByPayIdIn(List<String> payIdList) {
        return purchasePayDetailRepository.findByPayIdIn(payIdList);
    }

    /**
     * 采购订单付款
     * @param purchaseOrderDto
     * @return
     */
    @Override
    public PurchaseOrderDto pay(PurchaseOrderDto purchaseOrderDto) {

        if (CollectionUtils.isEmpty(purchaseOrderDto.getPurchaseOrderDetailList())) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_DETAIL_EMPTY);
        }

        String orderId = purchaseOrderDto.getOrderId();
        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderMasterRepository.findOne(orderId);
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }

        // 添加状态判断
        if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.ACCESSED.getCode())
                && !purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.PAID_PART.getCode())) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_STATUS_ERROR);
        }
        if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.PAID_PART.getCode())) {
            purchaseOrderMaster.setOrderStatus(PurchaseOrderStatusEnum.PAID_PART.getCode());
            purchaseOrderMasterRepository.save(purchaseOrderMaster);
        }

        // 采购订单 -> n次付款(PurchaseOrderPay) -> n*n个确认详情(PurchasePayDetail)

        List<PurchasePayDetail> purchasePayDetailList = purchaseOrderDto
                .getPurchaseOrderDetailList().stream()
                .map(e -> {
                    PurchasePayDetail purchasePayDetail = new PurchasePayDetail();
                    BeanUtils.copyProperties(e, purchasePayDetail);
                    return purchasePayDetail;
                }).collect(Collectors.toList());

        // 此次确认的商品总价
        BigDecimal payAmount = purchaseOrderDto.getPayAmount();
        String payId = KeyUtil.genUniqueKey();
        String comment = purchaseOrderDto.getComment();

        List<PurchasePayDetail> saveList = new ArrayList<>();
        try {
            // 确认详情入库
            for (PurchasePayDetail detail : purchasePayDetailList) {
                Shop shop = shopService.findOne(detail.getProductId());
                detail.setDetailId(KeyUtil.genUniqueKey());
                detail.setPayId(payId);
                detail.setProductName(shop.getShopName());
                detail.setProductJancode(shop.getShopJan());

                // 查询出该供应商对该商品的报价
                ProviderProductRelat providerProductRelat = providerProductRelatService.findByProviderIdAndProductId(detail.getProductId(), purchaseOrderDto.getProviderId());
                if (providerProductRelat == null) {
                    throw new GlobalException(ResultEnum.PROVIDER_PRODUCT_RELAT_ERROR);
                }
                BigDecimal supplyPrice = providerProductRelat.getPurchasePrice();
                // 设置供货商的报价，并非商品的价格
                detail.setProductPrice(supplyPrice);
                if (supplyPrice == null) {
                    throw new GlobalException(ResultEnum.PROVIDER_PRODUCT_PRICE_EMPTY);
                }

                // 计算购物车该商品总价
//                payAmount = payAmount.add(new BigDecimal(detail.getProductQuantity()).multiply(supplyPrice));

                // 采购订单详情入库
                saveList.add(detail);
            }

            // 确认详情批量入库
            purchasePayDetailRepository.save(saveList);

            // 保存确认记录
            PurchaseOrderPay purchaseOrderPay = new PurchaseOrderPay();
            purchaseOrderPay.setPayId(payId);
            purchaseOrderPay.setOrderId(orderId);
            purchaseOrderPay.setPayTime(new Date());
            purchaseOrderPay.setPayAmount(payAmount);
            purchaseOrderPay.setComment(comment);

            // 确认记录入库
            purchaseOrderPayRepository.save(purchaseOrderPay);


            // 凭证入库
            List<String> certUrlList = purchaseOrderDto.getCertUrlList();
            if (!CollectionUtils.isEmpty(certUrlList)) {

                List<PayCert> payCertList = new ArrayList<>();

                for (String certUrl : certUrlList) {
                    PayCert payCert = new PayCert();
                    payCert.setPayId(payId);
                    payCert.setPayCert(certUrl);
                    payCertList.add(payCert);
                }
                payCertService.save(payCertList);
            }

        } catch (Exception e) {
            throw new GlobalException(ResultEnum.UPDATE_PURCHASE_ORDER_FAIL);
        }

        PurchaseOrderDto resultDto = PurchaseOrderMaster2PurchaseOrderDtoConverter.convert(purchaseOrderMaster);
        return resultDto;
    }

    @Override
    public void finish(String orderId) {
        PurchaseOrderMaster purchaseOrderMaster = purchaseOrderMasterRepository.findByOrderId(orderId);
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }
        if (!purchaseOrderMaster.getOrderStatus().equals(PurchaseOrderStatusEnum.PAID.getCode())) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_STATUS_ERROR);
        }
        purchaseOrderMaster.setOrderStatus(PurchaseOrderStatusEnum.FINISHED.getCode());
        purchaseOrderMasterRepository.save(purchaseOrderMaster);
    }

    @Override
    public PurchaseOrderMaster save(PurchaseOrderMaster purchaseOrderMaster) {
        return purchaseOrderMasterRepository.save(purchaseOrderMaster);
    }

    @Override
    public List<PurchaseOrderMaster> findByCreateTimeBetween(Date beginTime, Date endTime) {
        return purchaseOrderMasterRepository.findByCreateTimeBetween(beginTime, endTime);
    }

    @Override
    public List<PurchaseOrderDetail> findDetailByOrderIdIn(List<String> orderIdList) {
        return purchaseOrderDetailRepository.findByOrderIdIn(orderIdList);
    }

    @Override
    public Page<PurchaseOrderDetail> findDetailByOrderIdInAndProductId(List<String> orderIdList, Integer productId, Pageable pageable) {
        return purchaseOrderDetailRepository.findByOrderIdInAndProductId(orderIdList, productId, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void verify(String orderId, String certUrls) {

        PurchaseOrderMaster purchaseOrderMaster = findByOrderId(orderId);
        if (purchaseOrderMaster == null) {
            throw new GlobalException(ResultEnum.PURCHASE_ORDER_NOT_EXIST);
        }

        if (!StringUtils.isEmpty(certUrls) && certUrls.contains("\"")) {
            certUrls = certUrls.replace("\"", "");
        }
        String[] certUrlArray = certUrls.split(",");
        List<String> certUrlList = Arrays.asList(certUrlArray);

        // 凭证入库
        List<VerifyCert> verifyCertList = new ArrayList<>();
        for (String certUrl : certUrlList) {
            VerifyCert verifyCert = new VerifyCert();
            verifyCert.setOrderId(orderId);
            verifyCert.setCertUrl(certUrl);
            verifyCertList.add(verifyCert);
        }
        verifyCertService.save(verifyCertList);

        // 修改采购订单状态
        purchaseOrderMaster.setOrderStatus(PurchaseOrderStatusEnum.CONFIRMED.getCode());
        save(purchaseOrderMaster);

        /**
         * 增加在途库存
         */
        List<PurchaseOrderDetail> purchaseOrderDetailList = findDetailByOrderId(orderId);
        repositoryProductService.increaseWayStock(purchaseOrderDetailList);
    }

    @Override
    public void deletePurchaseOrder(String orderId) {
        purchaseOrderMasterRepository.delete(orderId);
    }

    @Override
    public void deletePurchaseOrderDetailByOrderId(String orderId) {
        purchaseOrderDetailRepository.deleteByOrderId(orderId);
    }

    @Override
    public Page<PurchaseOrderMaster> findByCriteria(String criteria, List<Integer> statusList, Pageable pageable) {
        return purchaseOrderMasterRepository.findByCriteria(criteria, statusList, pageable);
    }
}
