package com.jizhangyl.application.other;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author 曲健磊
 * @date 2018年9月26日 上午11:36:28
 * @description 测试List
 */
public class ListTest {

    @Test
    public void testSubList() {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 1021; i++) {
            list.add(i);
        }
        
        int page = 11; // 第几页
        int size = 1000; // 每页50条
        
        System.out.println("总页数:" + ((list.size() / size) + 1));
        System.out.println(list.subList((page - 1)*size, page * size > list.size() ? list.size() : page * size));
    }
    
}
