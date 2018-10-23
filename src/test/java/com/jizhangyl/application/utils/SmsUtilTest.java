package com.jizhangyl.application.utils;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.jizhangyl.application.MainApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.Assert.*;

/**
 * @author 杨贤达
 * @date 2018/9/4 13:56
 * @description
 */
@Slf4j
@Component
public class SmsUtilTest extends MainApplicationTests {

    @Autowired
    private SmsUtil smsUtil;

    @Test
    public void remainNotify() {
        SendSmsResponse response = smsUtil.remainNotify("15990128390", 10000, "100861001010000");
        log.info("Code = {}", response.getCode());
        log.info("Message = {}", response.getMessage());
        log.info("RequestId = {}", response.getRequestId());
        log.info("BizId = {}", response.getBizId());
        if (response.getCode() != null && response.getCode().equals("OK")) {
            log.info("发送成功");
        } else {
            log.info("发送失败");
        }
    }

    /**
     * 18516184686
     */
    @Test
    public void expressNotify() {
        SendSmsResponse response = smsUtil.expressNotify("16630322894", "BS057122647CN", "100861001010001");
        log.info("Code = {}", response.getCode());
        log.info("Message = {}", response.getMessage());
        log.info("RequestId = {}", response.getRequestId());
        log.info("BizId = {}", response.getBizId());
        if (response.getCode() != null && response.getCode().equals("OK")) {
            log.info("发送成功");
        } else {
            log.info("发送失败");
        }
    }
}