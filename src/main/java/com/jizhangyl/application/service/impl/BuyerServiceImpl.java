package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.BuyerInfo;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.dataobject.primary.WxuserSender;
import com.jizhangyl.application.service.BuyerService;
import com.jizhangyl.application.service.WxuserSenderService;
import com.jizhangyl.application.service.WxuserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/9/7 16:52
 * @description 代购信息获取
 */
@Service
public class BuyerServiceImpl implements BuyerService {

    @Autowired
    private WxuserSenderService wxuserSenderService;

    @Autowired
    private WxuserService wxuserService;


    /**
     * 获取代购的信息列表和发件人列表
     * @param orderMasterList
     * @return
     */
    @Override
    public BuyerInfo findBuyerInfo(List<OrderMaster> orderMasterList) {

        List<String> openidList = orderMasterList.stream()
                .map(e -> e.getBuyerOpenid())
                .collect(Collectors.toList());

        List<Wxuser> wxuserList = wxuserService.findByOpenIdIn(openidList);
        List<WxuserSender> wxuserSenderList = wxuserSenderService.findByOpenidIn(openidList);

        BuyerInfo buyerInfo = new BuyerInfo();
        buyerInfo.setWxuserList(wxuserList);
        buyerInfo.setWxuserSenderList(wxuserSenderList);

        return buyerInfo;
    }
}
