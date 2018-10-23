package com.jizhangyl.application.utils;

import java.util.ArrayList;
import java.util.List;

import com.jizhangyl.application.entity.PageEntity;
import com.jizhangyl.application.entity.PageParam;


public class PageTest {

    public static void main(String[] args) {
        List<People> peopleList = new ArrayList<People>();
        People p1 = new People(1, "张三");
        People p2 = new People(2, "李四");
        People p3 = new People(3, "王五");
        People p4 = new People(4, "赵六");
        People p5 = new People(5, "麻子");
        
        People p6 = new People(6, "Array");
        People p7 = new People(7, "Mary");
        People p8 = new People(8, "Hello");
        People p9 = new People(9, "Kitty");
        People p10 = new People(10, "oh");
        
        People p11 = new People(11, "God");
        
        peopleList.add(p1);
        peopleList.add(p2);
        peopleList.add(p3);
        peopleList.add(p4);
        peopleList.add(p5);
        peopleList.add(p6);
        peopleList.add(p7);
        peopleList.add(p8);
        peopleList.add(p9);
        peopleList.add(p10);
        peopleList.add(p11);
        
        int page = 3; // 第1页
        int size = 5; // 每页5条
        
        PageParam  pageParam = new PageParam(page - 1, size);
        PageEntity<People> pageEntity = PageUtils.startPage(peopleList, pageParam);
        
        List<People> pList = pageEntity.getPageList();      // 1. 这一页的数据
        int number = pageEntity.getNumber();                // 2. 这一页的页码
        int curSize = pageEntity.getSize();                 // 3. 这一页有多少条记录
        int totalPage = pageEntity.getTotalPages();         // 4. 共多少页
        int totalElements = pageEntity.getTotalElements();  // 5. 共多少条记录
        boolean hasContent = pageEntity.hasContent();       // 6. 这一页是否有内容
        boolean isFirst = pageEntity.isFirst();             // 7. 是否是第一页
        boolean isLast = pageEntity.isLast();               // 8. 是否是最后一页
        boolean hasPrevious = pageEntity.hasPrevious();     // 9. 是否有前一页
        boolean hasNext = pageEntity.hasNext();             // 10.是否有后一页
        
        System.out.println("当前这一页的数据: \r\n" + pList);
        System.out.println("当前是第" + number + "页");
        System.out.println("这一页有" + curSize + "条记录");
        System.err.println("共" + totalPage + "页");
        System.err.println("共" + totalElements + "条记录");
        System.out.println(hasContent ? "当前这一页有内容" : "当前这一页没有内容");
        System.out.println(isFirst ? "当前这一页是第一页" : "当前这一页不是第一页");
        System.out.println(isLast ? "当前这一页是最后一页" : "当前这一页不是最后一页");
        System.out.println(hasPrevious ? "有上一页" : "没有上一页");
        System.out.println(hasNext ? "有下一页" : "没有下一页");
    }
}

class People {
    
    private int id;
    
    private String name;

    public People() {}

    public People(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "People [id=" + id + ", name=" + name + "]\r\n";
    }
}
