package com.jizhangyl.application.lambda;

import com.jizhangyl.application.MainApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/9 18:23
 * @description
 */
@Slf4j
public class FilterExample {

    public static void main(String[] args) {
        Pojo p1 = new Pojo();
        p1.setName("yang");
        p1.setAge(24);

        Pojo p2 = new Pojo();
        p2.setName("xian");
        p2.setAge(24);

        Pojo p3 = new Pojo();
        p3.setName("da");
        p3.setAge(24);

        Pojo p4 = new Pojo();
        p4.setName("yangxianda");
        p4.setAge(23);

        List<Pojo> pojoList = new ArrayList<>();
        pojoList.add(p1);
        pojoList.add(p2);
        pojoList.add(p3);
        pojoList.add(p4);

        log.info("prePojoList = {}", pojoList);


        List<Pojo> resultList = pojoList.stream().filter(e -> {
            if (e.getAge().equals(24)) {
                e.setAge(888);
            }
            return true;
        }).collect(Collectors.toList());

        log.info("pojoList = {}", pojoList);
        log.info("resultList = {}", resultList);
    }
}
