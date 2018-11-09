package com.jizhangyl.application.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jizhangyl.application.dataobject.primary.BuyerInfo;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.primary.Shop;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.dataobject.primary.WxuserSender;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.dto.InstockOrderDTO;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.service.BuyerService;
import com.jizhangyl.application.service.OrderDetailService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.service.WxuserAddrService;
import com.jizhangyl.application.utils.DateUtil;

/**
 * @author 曲健磊
 * @date 2018年11月9日 下午2:19:56
 * @description 导出现货订单的转换类
 */
@Component
public class OrderMaster2InstockOrderDTOConverter {

    @Autowired
    private WxuserAddrService wxuserAddrService;

    @Autowired
    private OrderDetailService orderDetailService;
    
    @Autowired
    private ShopService shopService;

    @Autowired
    private BuyerService buyerService;

    public List<InstockOrderDTO> convert(List<OrderMaster> orderMasterList) {
        List<InstockOrderDTO> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(orderMasterList)) {
            return resultList;
        }
        // 获取所有的订单明细
        List<String> orderIds = orderMasterList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
        List<OrderDetail> detailList = orderDetailService.findByOrderIdIn(orderIds);
        Map<String, List<OrderDetail>> detailMap = detailList.stream()
                                                             .collect(Collectors.groupingBy(e -> e.getOrderId()));

        // 获取订单里面所有的商品
        List<Integer> shopIds = detailList.stream()
                                          .map(e -> Integer.valueOf(e.getProductId()))
                                          .collect(Collectors.toList());
        List<Shop> shopList = shopService.findByIdIn(shopIds);
        Map<Integer, List<Shop>> shopMap = shopList.stream()
                                                   .collect(Collectors.groupingBy(e -> e.getId()));
        // 收件人
        List<Integer> recvAddrIdList = orderMasterList.stream()
                                                      .map(e -> e.getRecipientAddrId())
                                                      .collect(Collectors.toList());
        List<WxuserAddr> wxuserAddrList = wxuserAddrService.findByIdIn(recvAddrIdList);
        Map<Integer, List<WxuserAddr>> recvAddrMap = wxuserAddrList.stream()
                                                                  .collect(Collectors.groupingBy(e -> e.getId()));
        
        // 发件人
        BuyerInfo buyerInfo = buyerService.findBuyerInfo(orderMasterList);
        List<Wxuser> wxuserList = buyerInfo.getWxuserList();
        Map<String, List<Wxuser>> wxuserMap = wxuserList.stream()
                                                        .collect(Collectors.groupingBy(e -> e.getOpenId()));
        List<WxuserSender> wxuserSenderList = buyerInfo.getWxuserSenderList();
        Map<Integer, List<WxuserSender>> wxuserSenderMap = wxuserSenderList.stream()
                                                                          .collect(Collectors.groupingBy(e -> e.getId()));
        
        for (OrderMaster orderMaster : orderMasterList) {
            InstockOrderDTO instockOrder = new InstockOrderDTO();
            // 1.设置订单时间
            instockOrder.setCreateTime(DateUtil.dateToString(orderMaster.getCreateTime()));
            // 2.设置集栈订单编号
            instockOrder.setJzOrderId(orderMaster.getOrderId());
            // 3.设置商品jancode
            List<OrderDetail> detail = detailMap.get(orderMaster.getOrderId());
            String jancodes = "";
            for (OrderDetail orderDetail : detail) {
                List<Shop> tempShopList = shopMap.get(Integer.valueOf(orderDetail.getProductId()));
                Shop tmpShop = tempShopList.get(0);
                jancodes = jancodes + tmpShop.getShopJan() + "*" + orderDetail.getProductQuantity() + "【";
            }
            instockOrder.setJanCodes(jancodes);
            // 4.设置订单状态
            instockOrder.setOrderStatus(OrderStatusEnum.transStr(orderMaster.getOrderStatus()));
            // 5.设置买手邀请码
            Wxuser wxuser = wxuserMap.get(orderMaster.getBuyerOpenid()).get(0);
            instockOrder.setInviteCode(wxuser.getInviteCode());
            // 6.设置买手名字
            instockOrder.setBuyerName(orderMaster.getBuyerName());
            // 7.设置物流承运方
            instockOrder.setExpCorp("");
            // 8.设置快递单号
            instockOrder.setExpressNumber(orderMaster.getExpressNumber());
            // 9.设置买家昵称
            instockOrder.setBuyerNickName(wxuser.getNickName());
            // 10.设置配送范围
            instockOrder.setDeliveryRange("");
            // 11.设置收件省
            WxuserAddr wxuserAddr = recvAddrMap.get(orderMaster.getRecipientAddrId()).get(0);
            instockOrder.setReciverProvince(wxuserAddr.getProvince());
            // 12.设置收件市
            instockOrder.setReciverCity(wxuserAddr.getCity());
            // 13.设置收件区
            instockOrder.setReciverArea(wxuserAddr.getArea());
            // 14.设置收件街道
            instockOrder.setReciverStreet(wxuserAddr.getDetailAddr());
            // 15.设置收件姓名
            instockOrder.setReciverName(wxuserAddr.getReceiver());
            // 16.设置收件公司
            instockOrder.setReciverCompany("");
            // 17.设置收件手机
            instockOrder.setReciverMobile(wxuserAddr.getPhone());
            // 18.设置收件座机
            instockOrder.setReciverPhone("");
            // 19.设置收件邮编
            instockOrder.setReciverZipCode("");
            // 20.设置卖家备注
            instockOrder.setSellerRemark("");
            // 21.设置买家备注
            instockOrder.setBuyerRemark("");
            // 22.设置订单编号
            instockOrder.setOrderId("");
            // 23.设置打印编号
            instockOrder.setPrintId("");
            // 24.设置订单物品(打包识别码*数量)
            String packCodes = "";
            for (OrderDetail orderDetail : detail) {
                List<Shop> tempShopList = shopMap.get(Integer.valueOf(orderDetail.getProductId()));
                Shop tmpShop = tempShopList.get(0);
                packCodes = packCodes + tmpShop.getPackCode() + "*" + orderDetail.getProductQuantity() + "【";
            }
            instockOrder.setPackCodes(jancodes);
            // 25.设置自定义备注
            instockOrder.setDefineRemark("");
            // 26.设置打印份数
            instockOrder.setPrintNums("");
            // 27.设置模板名
            instockOrder.setTemplateName("");
            // 28.设置重量
            instockOrder.setWeight("");
            // 29.设置价格
            instockOrder.setPrice("");
            // 30.设置卖家昵称
            instockOrder.setSellerNickName("");
            // 31.设置发件省
            instockOrder.setSenderProvince("天津市");
            // 32.设置发件市
            instockOrder.setSenderCity("天津市");
            // 33.设置发件区
            instockOrder.setSenderArea("南开区");
            // 34.设置发件街道
            instockOrder.setSenderStreet("长江道");
            // 35.设置发件姓名
            WxuserSender wxuserSender = wxuserSenderMap.get(orderMaster.getBuyerAddrId()).get(0);
            instockOrder.setSenderName(wxuserSender.getSender());
            // 36.设置发件公司
            instockOrder.setSenderCompany("");
            // 37.设置发件手机
            instockOrder.setSenderMobile(wxuserSender.getPhone());
            // 38.设置发件座机
            instockOrder.setSenderPhone("");
            // 39.设置发件邮编
            instockOrder.setSenderZipCode("");
            // 40.设置大头笔
            instockOrder.setBigPen("");
            resultList.add(instockOrder);
        }
        return resultList;
    }
}
