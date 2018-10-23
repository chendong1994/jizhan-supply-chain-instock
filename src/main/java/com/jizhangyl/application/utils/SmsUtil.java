package com.jizhangyl.application.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.jizhangyl.application.config.SmsConfig;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 杨贤达
 * @date 2018/9/4 11:45
 * @description
 */
@Component
public class SmsUtil {

    @Autowired
    private SmsConfig smsConfig;

    /**
     *
     * 物理单号余量不足提醒
     * @param phoneNumber
     * @param num
     * @param outId
     * @return
     */
    public SendSmsResponse remainNotify(String phoneNumber, Integer num, String outId) {

        try {
            // 可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            // 初始化 acsClient, 暂不支持 region 化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", smsConfig.getProduct(), smsConfig.getDomain());
            IAcsClient acsClient = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();

            request.setPhoneNumbers(phoneNumber);
            request.setSignName(smsConfig.getSignName());
            request.setTemplateCode(smsConfig.getTemplateCode().get("remainNotify"));
            request.setTemplateParam("{\"num\":\"" + num + "\"}");
            request.setOutId(outId);

            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            return sendSmsResponse;

        } catch (Exception e) {
            if (e instanceof ClientException || e instanceof InterruptedException) {
                throw new GlobalException(ResultEnum.ALIYUN_SMS_ERROR);
            } else {
                throw new GlobalException(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
            }
        }
    }


    /**
     * 订单付款发送物流单号
     * @param phoneNumber
     * @param code
     * @param outId
     * @return
     */
    public SendSmsResponse expressNotify(String phoneNumber, String code, String outId) {

        try {
            // 可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            // 初始化 acsClient, 暂不支持 region 化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", smsConfig.getProduct(), smsConfig.getDomain());
            IAcsClient acsClient = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();

            request.setPhoneNumbers(phoneNumber);
            request.setSignName(smsConfig.getSignName());
            request.setTemplateCode(smsConfig.getTemplateCode().get("expressNotify"));
            request.setTemplateParam("{\"code\":\"" + code + "\"}");
            request.setOutId(outId);

            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            return sendSmsResponse;

        } catch (Exception e) {
            if (e instanceof ClientException || e instanceof InterruptedException) {
                throw new GlobalException(ResultEnum.ALIYUN_SMS_ERROR);
            } else {
                throw new GlobalException(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
            }
        }
    }



    /**
     * 证件审核被驳回通知
     * @param phoneNumber
     * @param code
     * @param outId
     * @return
     */
    public SendSmsResponse certRefuse(String phoneNumber, String code, String outId) {

        try {
            // 可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            // 初始化 acsClient, 暂不支持 region 化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", smsConfig.getProduct(), smsConfig.getDomain());
            IAcsClient acsClient = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();

            request.setPhoneNumbers(phoneNumber);
            request.setSignName(smsConfig.getSignName());
            request.setTemplateCode(smsConfig.getTemplateCode().get("certRefuse"));
            request.setTemplateParam("{\"code\":\"" + code + "\"}");
            request.setOutId(outId);

            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            return sendSmsResponse;

        } catch (Exception e) {
            if (e instanceof ClientException || e instanceof InterruptedException) {
                throw new GlobalException(ResultEnum.ALIYUN_SMS_ERROR);
            } else {
                throw new GlobalException(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
            }
        }
    }

    /**
     * 提醒用户上传证件短信通知
     * @param phoneNumber
     * @param num
     * @param outId
     * @return
     */
    public SendSmsResponse orderNotify(String phoneNumber, String num, String outId) {

        try {
            // 可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            // 初始化 acsClient, 暂不支持 region 化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", smsConfig.getProduct(), smsConfig.getDomain());
            IAcsClient acsClient = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();

            request.setPhoneNumbers(phoneNumber);
            request.setSignName(smsConfig.getSignName());
            request.setTemplateCode(smsConfig.getTemplateCode().get("orderNotify"));
            request.setTemplateParam("{\"num\":\"" + num + "\"}");
            request.setOutId(outId);

            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            return sendSmsResponse;

        } catch (Exception e) {
            if (e instanceof ClientException || e instanceof InterruptedException) {
                throw new GlobalException(ResultEnum.ALIYUN_SMS_ERROR);
            } else {
                throw new GlobalException(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
            }
        }
    }

    /**
     * TODO 短信提醒整合到一个方法
     * 参数：Map
     */
}
