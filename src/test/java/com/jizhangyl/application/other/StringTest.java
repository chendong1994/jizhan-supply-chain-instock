package com.jizhangyl.application.other;

import org.junit.Test;

/**
 * @author 曲健磊
 * @date 2018年9月26日 上午10:49:31
 * @description 测试String类的方法
 */
public class StringTest {

    @Test
    public void testContains() {
        String expressNum = "BS0567123903CN";
        String deliveryNum = "123456787678";
        String inviteCode = "C111111";
        
        String content = "567";
        
        if (expressNum.contains(content)) {
            System.out.println("物流单号包含content");
        } else {
            System.out.println("物流单号不包含content");
        }
        
        if (deliveryNum.contains(content)) {
            System.out.println("航班提运单号包含content");
        } else {
            System.out.println("航班提运单号不包含content");
        }
        
        if (inviteCode.contains(content)) {
            System.out.println("邀请码包含content");
        } else {
            System.out.println("邀请码不包含content");
        }
        
        
    }
    
}
