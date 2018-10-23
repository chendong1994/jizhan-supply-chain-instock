package com.jizhangyl.application.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * @author 曲健磊
 * @date 2018年10月7日 下午6:50:22
 * @description 取某个集合中前n大的元素
 */
public class TopNTest {
    public static void main(String[] args) {
        int elementNums = 1000000; // 元素的个数
        Integer[] input = new Integer[elementNums];
        Random rand = new Random();
        for (int i = 0; i < elementNums; i++) {
            input[i] = rand.nextInt();
        }
        int n = 5;
        System.out.println("从一百万个数中找出最大的前" + n + "个数,性能对比:");
        
        Long startTime = System.currentTimeMillis();
        List<Integer> topN = findTopN(input, n);
        Long endTime = System.currentTimeMillis();
        for (Integer item : topN) {
            System.out.println(item);
        }
        System.out.println("堆采用的时间:" + (endTime - startTime) + "ms");
        
        startTime = System.currentTimeMillis();
        topN = getTopN(input, n);
        endTime = System.currentTimeMillis();
        for (Integer item : topN) {
            System.out.println(item);
        }
        System.out.println("传统排序的时间:" + (endTime - startTime) + "ms");
    }

    /**
     * 使用堆来保存前n大的,最后只对这n个数进行排序
     * @param input 数组
     * @param n 前n个数
     * @return 筛选后的list集合
     */
    public static List<Integer> findTopN(Integer[] input, int n) {
        if (input == null || input.length == 0 || n < 1) {
            throw new RuntimeException();
        }
        
        PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(n);
        for (int i = 0; i < input.length; i++) {
            if (minHeap.size() < n) {
                minHeap.offer(input[i]);
            } else if (minHeap.peek() < input[i]) {
                minHeap.poll();
                minHeap.offer(input[i]);
            }
        }
        List<Integer> results = new ArrayList<Integer>(minHeap);
        Collections.sort(results, Comparator.reverseOrder());
        return results;
    }
    
    /**
     * 对所有的数排序后取前n个
     * @param input 数组
     * @param n 前n个数
     * @return 筛选后的list集合
     */
    public static List<Integer> getTopN(Integer[] input, int n) {
        if (input == null || input.length == 0 || n < 1) {
            throw new RuntimeException();
        }
        Arrays.sort(input, Comparator.reverseOrder());
        List<Integer> results = new ArrayList<Integer>(n);
        for (int i = 0; i < n; i++) {
            results.add(input[i]);
        }
        return results;
    }
}
