package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.ExpressNum;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.primary.PurchaseOrderDetail;
import com.jizhangyl.application.dataobject.primary.RepositoryDelivery;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.RepositoryDeliveryRepository;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.RepositoryDeliveryService;
import com.jizhangyl.application.service.RepositoryProductService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/9/5 11:02
 * @description 仓库发货
 */
@Service
public class RepositoryDeliveryServiceImpl implements RepositoryDeliveryService {

    @Autowired
    private OrderMasterService orderMasterService;

    @Autowired
    private RepositoryDeliveryRepository repositoryDeliveryRepository;

    @Autowired
    private RepositoryDeliveryService repositoryDeliveryService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RepositoryProductService repositoryProductService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delivery(List<ExpressNum> expressNumList) {

        List<String> numList = expressNumList.stream().map(e -> e.getExpNum()).collect(Collectors.toList());

        List<OrderMaster> orderMasterList = orderMasterService.findByExpressNumberIn(numList);



        // 单号若对应多个订单，挑选出满发货状态的那个订单号码
        // 如果包含之前已经发货的订单直接忽略

        Map<String, List<OrderMaster>> map = getOrderMap(numList, orderMasterList);

        Iterator<Map.Entry<String, List<OrderMaster>>> iterator = map.entrySet().iterator();

        // 最终需要发货的 orderMasterList
        List<OrderMaster> resultOrderMasterList = new ArrayList<>();

        while (iterator.hasNext()) {
            Map.Entry<String, List<OrderMaster>> entry = iterator.next();

            String expressNum = entry.getKey();
            List<OrderMaster> orderList = entry.getValue();

            // 未找到快递单号对应的订单
            if (CollectionUtils.isEmpty(orderList)) {
                throw new GlobalException(ResultEnum.EXP_NUM_RELAT_EMPTY.getCode(),
                        String.format(ResultEnum.EXP_NUM_RELAT_EMPTY.getMessage(), expressNum));
            } else if (orderList.size() == 1) {
                // 找到一个的情况
                OrderMaster om = orderList.get(0);
                // 已经取消的订单
                if (om.getOrderStatus().equals(OrderStatusEnum.CANCELED.getCode())) {
                    throw new GlobalException(ResultEnum.ORDER_CANCELED.getCode(),
                            String.format(ResultEnum.ORDER_CANCELED.getMessage(), om.getExpressNumber()));
                }
                // 考虑是否已经发货的订单，该选项直接忽略

                // 为待发货的直接加入 list, 进行发货
                if (om.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {
                    resultOrderMasterList.add(om);
                }
            } else {
                // 找到多个的情况
                // 过滤掉快递单号对应的取消的订单或已经发货的订单
                List<Integer> orderStatusList = orderList.stream().map(e -> e.getOrderStatus()).collect(Collectors.toList());

                // 含已发货的且不含待发货的情况直接忽略，对立条件报错
                // 包含已经发货的同时必须保证该单号不能对应待发货，已经发货说明单号已经使用，若再对应到未发货必然报错。所以此条件的对立条件必然报错
                if (orderStatusList.contains(OrderStatusEnum.DELIVERED.getCode())
                        && orderStatusList.contains(OrderStatusEnum.PAID.getCode())) {
                    throw new GlobalException(ResultEnum.EXP_NUM_RELAT_ERROE.getCode(),
                            String.format(ResultEnum.EXP_NUM_RELAT_ERROE.getMessage(), expressNum));
                    // 含取消的且不含待发货
                } else if (orderStatusList.contains(OrderStatusEnum.CANCELED.getCode())
                        && !orderStatusList.contains(OrderStatusEnum.PAID.getCode())) {
                    throw new GlobalException(ResultEnum.ORDER_CANCELED.getCode(),
                            String.format(ResultEnum.ORDER_CANCELED.getMessage(), expressNum));
                }

                // 包含取消的且包含待发货的为正常情况
                List<OrderMaster> finalOrderList = orderList.stream().filter(e -> {
                    if (e.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());

                if (finalOrderList.size() == 1) {
                    resultOrderMasterList.addAll(finalOrderList);
                } else {
                    throw new GlobalException(ResultEnum.EXP_NUM_RELAT_ERROE.getCode(),
                            String.format(ResultEnum.EXP_NUM_RELAT_ERROE.getMessage(), expressNum));
                }

            }

        }

        /*for (OrderMaster orderMaster : orderMasterList) {
            if (orderMaster.getOrderStatus().equals(OrderStatusEnum.CANCELED.getCode())) {
                throw new GlobalException(ResultEnum.ORDER_CANCELED.getCode(),
                        String.format(ResultEnum.ORDER_CANCELED.getMessage(), orderMaster.getExpressNumber()));
            } else if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.TODO_DELIVERY.getCode())) {
                throw new GlobalException(ResultEnum.DELIVERY_ORDER_STATUS_ERROR.getCode(),
                        String.format(
                                ResultEnum.DELIVERY_ORDER_STATUS_ERROR.getMessage(),
                                orderMaster.getExpressNumber(),
                                orderMaster.getOrderStatusEnum().getMsg()));
            }
        }*/

        /*orderMasterList = orderMasterList.stream().filter(e -> {
            if (e.getOrderStatus().equals(OrderStatusEnum.TODO_DELIVERY.getCode())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());*/

//        List<String> orderIdList = orderMasterList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());


        if (!CollectionUtils.isEmpty(resultOrderMasterList)) {

            // 批量发货
//            orderMasterService.delivery(orderMasterList);
            orderMasterService.delivery(resultOrderMasterList);

            // 发货记录入库
            List<RepositoryDelivery> repositoryDeliveryList = new ArrayList<>();

            repositoryDeliveryList = resultOrderMasterList.stream().map(e -> {
                RepositoryDelivery repositoryDelivery = new RepositoryDelivery();
                String[] ignoreProperties = {"createTime", "updateTime"};
                BeanUtils.copyProperties(e, repositoryDelivery, ignoreProperties);
                repositoryDelivery.setDeliveryId(KeyUtil.genUniqueKey());
                repositoryDelivery.setOrderTime(e.getCreateTime());
                return repositoryDelivery;
            }).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(repositoryDeliveryList)) {
                List<RepositoryDelivery> result = repositoryDeliveryService.save(repositoryDeliveryList);
            }


            // 减仓库的当前和小程序的库存
            List<String> orderIdList = resultOrderMasterList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
            List<OrderDetail> orderDetailList = orderMasterService.findDetailByOrderIdIn(orderIdList);

//            shopService.decreaseStock(orderDetailList);

            List<PurchaseOrderDetail> purchaseOrderDetailList = orderDetailList.stream().map(e -> {
                PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
                BeanUtils.copyProperties(e, purchaseOrderDetail);
                purchaseOrderDetail.setProductId(Integer.valueOf(e.getProductId()));
                return purchaseOrderDetail;
            }).collect(Collectors.toList());

            repositoryProductService.decreaseProductStock(purchaseOrderDetailList);
        }
    }


    /**
     * 组装每个单号的对应的订单列表为 map
     * @param numList
     * @param orderMasterList
     * @return
     */
    private Map<String, List<OrderMaster>> getOrderMap(List<String> numList,
                                                             List<OrderMaster> orderMasterList) {
        Map<String, List<OrderMaster>> resultMap = new HashMap<>();

        for (String num : numList) {
            List<OrderMaster> list = new ArrayList<>();
            for (OrderMaster orderMaster : orderMasterList) {
                if (num.equals(orderMaster.getExpressNumber())) {
                    list.add(orderMaster);
                }
            }
            resultMap.put(num, list);
        }
        return resultMap;
    }

    @Transactional
    @Override
    public List<RepositoryDelivery> save(List<RepositoryDelivery> repositoryDeliveryList) {
        List<RepositoryDelivery> result = repositoryDeliveryRepository.saveAll(repositoryDeliveryList);
        if (CollectionUtils.isEmpty(result)) {
            throw new GlobalException(ResultEnum.DELIVERY_RECORD_SAVE_FAIL);
        }
        return result;
    }

    @Override
    public List<RepositoryDelivery> findByCreateTimeBetween(Date beginTime, Date endTime) {
        return repositoryDeliveryRepository.findByCreateTimeBetween(beginTime, endTime);
    }
}
