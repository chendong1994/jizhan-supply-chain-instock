package com.jizhangyl.application.factory;

import java.util.Comparator;

/**
 * @author 曲健磊
 * @date 2018年10月20日 下午12:48:17
 * @description 抽象比较器工厂(工厂方法)
 */
public abstract class ComparatorFactory<T> {

    /**
     * 创建比较器的抽象方法
     * @param paramEnum 排序的基准
     * @param orderEnum 升序降序
     * @return
     */
    public abstract Comparator<T> createComparator(Enum<?> paramEnum, Enum<?> orderEnum);
}
