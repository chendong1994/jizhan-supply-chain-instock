package com.jizhangyl.application.VO;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 杨贤达
 * @date 2018/8/7 16:26
 * @description
 */
@Data
public class WxuserAddrVO implements Serializable {

    private static final long serialVersionUID = 3066685158438419058L;

    private String receiver;

    private String receiverNickname;

    private String phone;

    private String area;

    private String detailAddr;
}
