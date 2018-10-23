package com.jizhangyl.application.dataobject;

import lombok.Data;

import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/9/7 16:50
 * @description
 */
@Data
public class BuyerInfo {

    private List<Wxuser> wxuserList;

    private List<WxuserSender> wxuserSenderList;
}
