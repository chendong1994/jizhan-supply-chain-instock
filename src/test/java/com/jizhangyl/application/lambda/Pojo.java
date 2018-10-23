package com.jizhangyl.application.lambda;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author 杨贤达
 * @date 2018/8/9 18:24
 * @description
 */
public class Pojo {

    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Pojo() {
    }

    public Pojo(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

    public static void main(String[] args) {
        Pojo p1 = new Pojo();
        p1.setName("yang");
        p1.setAge(24);

        System.out.println(p1.toString());
    }
}
