package com.jizhangyl.application.controller;

import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.config.WechatAccountConfig;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.PayService;
import com.jizhangyl.application.utils.PayUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨贤达
 * @date 2018/8/11 13:14
 * @description
 */
@Slf4j
@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private OrderMasterService orderMasterService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private PayService payService;

    @Autowired
    private WechatAccountConfig accountConfig;

    /**
     * 发起支付
     * 调用统一下单接口，获取“预支付交易会话标识”
     */
    @ResponseBody
    @PostMapping("/unifiedOrder")
    public ResultVO unifiedOrder(@RequestParam("orderId") String orderId,
                                 HttpServletRequest request) throws WxPayException {
        // 1. 查询订单
        OrderDto orderDto = orderMasterService.findOne(orderId);
        if (orderDto == null) {
            throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
        }
        WxPayUnifiedOrderRequest orderRequest = payService.create(orderDto, request);
        // 2. 调用统一下单接口
        WxPayUnifiedOrderResult orderResult = wxPayService.unifiedOrder(orderRequest);
        /*map.put("orderRequest", orderRequest);
        map.put("orderResult", orderResult);
        map.put("timeStamp", PayUtil.getTimeStamp());
        map.put("nonceStr", PayUtil.getNonceStr());

        // 3. 使用参数动态注入，将参数传给wx.requestPayment{}
        return new ModelAndView("pay/unifiedOrder", map);*/
        Map<String, String> result = new HashMap<>();


        String appId = orderResult.getAppid();
        String timeStamp = PayUtil.getTimeStamp();
        String nonceStr = PayUtil.getNonceStr();
        String pack = orderResult.getPrepayId();
        String signType = "MD5";

        String params = "appId=" + appId + "&nonceStr=" + nonceStr + "&package=prepay_id=" + pack + "&signType=" + signType + "&timeStamp=" + timeStamp + "&key=" + accountConfig.getMchKey();
        String paySign = DigestUtils.md5Hex(params).toUpperCase();

        result.put("appid", appId);
        result.put("timeStamp", timeStamp);
        result.put("nonceStr", nonceStr);
        result.put("package", "prepay_id=" + pack);
        result.put("signType", signType);
        result.put("paySign", paySign);

//        String paySign = DigestUtils.md5Hex("")

        return ResultVOUtil.success(result);
    }

    /**
     * 微信异步通知
     * （微信收款成功回调此接口通知我们）
     * @param notifyData
     * @return
     */
    @ResponseBody
    @PostMapping("/notify")
    public String notify(@RequestBody String notifyData) {
        // 调用退款接口
        payService.notify(notifyData);

        // 返回给微信处理结果
        /*
        <xml>
            <return_code><![CDATA[SUCCESS]]></return_code>
            <return_msg><![CDATA[OK]]></return_msg>
        </xml>
         */
        String xmlResult = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
        return xmlResult;
    }

    public static void main(String[] args) {
        String paySign = "appId=wxd678efh567hg6787&nonceStr=5K8264ILTKCH16CQ2502SI8ZNMTM67VS&package=prepay_id=wx2017033010242291fcfe0db70013231072&signType=MD5&timeStamp=1490840662&key=qazwsxedcrfvtgbyhnujmikolp111111";
        System.out.println(DigestUtils.md5Hex(paySign).toUpperCase());
    }
}
