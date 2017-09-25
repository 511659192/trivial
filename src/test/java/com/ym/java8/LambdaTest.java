package com.ym.java8;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by yangm on 2017/9/8.
 */
public class LambdaTest {

    class WhatThis {

        public void whatThis(){
            //转全小写
            List<String> proStrs = Arrays.asList(new String[]{"Ni","Hao","Lambda"});
            List<String> execStrs = proStrs.stream().map(str -> {
                System.out.println(this.getClass().hashCode());
                return str.toLowerCase();
            }).collect(Collectors.toList());
            execStrs.forEach(System.out::println);
        }
    }

    @Test
    public void testThis() throws Exception {
        WhatThis wt = new WhatThis();
        System.out.println(wt.getClass().hashCode());
        wt.whatThis();
    }

    @Test
    public void testCollect() throws Exception {
        List<Integer> nums = Lists.newArrayList(1,1,null,2,3,4,null,5,6,7,8,9,10);
        List<Integer> numsWithoutNull = nums.stream().filter(num -> num != null).
                collect(() -> new ArrayList<Integer>(),
                        (list, item) -> {
                            System.out.println("list " + JSON.toJSONString(list));
                            list.add(item);
                        },
                        (list1, list2) -> {
                            System.out.println("list1 " + JSON.toJSONString(list1) + " list2 " + JSON.toJSONString(list2));
//                            list1.addAll(list2);
                        });
        System.out.println(JSON.toJSONString(numsWithoutNull));
    }

    @Test
    public void testReduce() throws Exception {
        List<Integer> ints = Lists.newArrayList(1,2,3,4,5,6,7,8,9,10);
        System.out.println("ints sum is:" + ints.stream().reduce((sum, item) -> {
            System.out.println("sum " + sum + " item " + item);
            return sum + item;
        }).get());
    }

    @Test
    public void testReduce2() throws Exception {
        List<Integer> ints = Lists.newArrayList(1,2,3,4,5,6,7,8,9,10);
        System.out.println("ints sum is:" + ints.stream().reduce(0, (sum, item) -> {
            System.out.println("sum " + sum + " item " + item);
            return sum + item;
        }));
    }

    @Test
    public void testCollectors() throws Exception {
        List<String>strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
        List<String> filtered = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());

        System.out.println("筛选列表: " + filtered);
        String mergedString = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining(", "));
        System.out.println("合并字符串: " + mergedString);
    }

    @Test
    public void testParallelStream() throws Exception {
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
// 获取空字符串的数量
        Long count = strings.parallelStream().filter(string -> string.isEmpty()).count();
        System.out.println(count);
    }
}
