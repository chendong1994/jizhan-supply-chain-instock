package com.jizhangyl.application.task;

import com.jizhangyl.application.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/10/9 17:55
 * @description
 */
@Slf4j
@Component
public class SynchronizationTask {

    @Autowired
    private ShopService shopService;

    /**
     * 每天晚上 00:00:00 执行同步, 更新商品信息
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void productDataSync() {
        shopService.productDataSync();
    }
}
