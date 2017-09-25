package com.ym.guava;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multiset;
import org.junit.Test;

import java.util.Set;

/**
 * Created by yangm on 2017/8/27.
 */
public class MultimapsTest {

    @Test
    public void testArrayListMultimap() {
        //create()：创建具有默认初始容量的新的空ArrayListMultimap
        ArrayListMultimap multimap = ArrayListMultimap.create();
        multimap.put("a", "1");
        multimap.put("a", "2");
        multimap.put("b", "3");
        multimap.put("b", "4");
        //获取mutlimap的键值
        Set<String> keys = multimap.keySet();
        System.out.println(keys); //输出：[b, a]
        //mutlimap提供了keys()方法，获取Multiset<String>
        Multiset<String> multiKeys = multimap.keys();
        System.out.println(multiKeys);//输出：[b x 2, a x 2]
        //通过multiKeys.elementSet()可以获取键值Set<String>
        //通过get()方法获取对应键所对应的value集合
        System.out.println(multimap.get("a"));//输出：[1,2]
        //如果添加相同的键值对
        multimap.put("a", "2");
        //键值所对应的value集合发生改变,输出：[1,2,2]
        System.out.println(multimap.get("a"));
    }

    @Test
    public void testHashMultimap() {
        //ArrayListMultimap允许重复的键值对
        ArrayListMultimap multimap = ArrayListMultimap.create();
        multimap.put("a", "1");
        multimap.put("a", "1");
        /**
         * create( Multimap<? extends K, ? extends V> multimap)
         * 构造与给定Mutimap有相同映射关系的HashMultimap实例
         */
        HashMultimap hashMultimap = HashMultimap.create(multimap);
        //如果给定Mutimap有重复的键值映射，构造后只会保留一个
        //输出：[1]，而不是[1,1]
        System.out.println(hashMultimap.get("a"));
    }
}
