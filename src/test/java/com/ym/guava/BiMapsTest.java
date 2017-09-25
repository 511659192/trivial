package com.ym.guava;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.junit.Test;

/**
 * Created by yangm on 2017/8/27.
 */
public class BiMapsTest {

    @Test
    public void testForcePut() {
        /**
         * create：构造一个空的HashBiMap实例。
         * HashBiMap：基于两个哈希表的BiMap接口的实现
         */
        BiMap<String, String> biMap = HashBiMap.create();
        biMap.put("testA", "Realfighter");
        try {
            /**
             * 通过put方法存入相同的value值，会抛出异常
             * java.lang.IllegalArgumentException: value already present
             */
            biMap.put("testB", "Realfighter");
        } catch (IllegalArgumentException e) {
            /**
             * forcePut()：相同value值允许传入
             */
            biMap.forcePut("testC", "Realfighter");
        }
        System.out.println(biMap.get("testB"));//能够获取到Realfighter
        System.out.println(biMap.get("testC"));//能够获取到Realfighter

    }

    @Test
    public void testInverse() {
        BiMap<String, String> biMap = HashBiMap.create();
        biMap.put("testA", "Realfighter");
        biMap.put("testB", "Realfighter2");
        /**
         * inverse()：进行键值对的反转，返回BiMap的一种双向映射关系
         */
        BiMap<String, String> biMap1 = biMap.inverse();
        System.out.println(biMap1.get("Realfighter"));//输出testA
        /**
         * BiMap调用inverse方法后，产生了一种关联关系
         * 所有对最初的BiMap的操作都会影响关联后的BiMap
         */
        //向最初biMap中存入testC
        biMap.put("testC", "Realfighter3");
        //biMap1中获取输出：testC
        System.out.println(biMap1.get("Realfighter3"));
        /**
         * 同样的，对关联后BiMap的操作也影响最初的BiMap
         */
        //向关联后的biMap1中反向存入testD
        biMap1.put("Realfighter4", "testD");
        //biMap中获取输出：Realfighter4
        System.out.println(biMap.get("testD"));
    }
}
