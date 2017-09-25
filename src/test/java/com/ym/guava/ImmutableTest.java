package com.ym.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by yangm on 2017/8/26.
 */
public class ImmutableTest {

    @Test
    public void testJDKImmutable(){
        List<String> list=new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        //[a, b, c]
        System.out.println(list);

        List<String> unmodifiableList= Collections.unmodifiableList(list);
        //[a, b, c]
        System.out.println(unmodifiableList);

        List<String> unmodifiableList1=Collections.unmodifiableList(Arrays.asList("a","b","c"));
        //[a, b, c]
        System.out.println(unmodifiableList1);

        String temp=unmodifiableList.get(1);
        //unmodifiableList [0]：b
        System.out.println("unmodifiableList [0]："+temp);

        list.add("baby");
        //list add a item after list:[a, b, c, baby]
        System.out.println("list add a item after list:"+list);
        //list add a item after unmodifiableList1:[a, b, c, baby]
        System.out.println("list add a item after unmodifiableList:"+unmodifiableList);

        unmodifiableList1.add("bb");
        System.out.println("unmodifiableList add a item after list:"+unmodifiableList1);

        unmodifiableList.add("cc");
        System.out.println("unmodifiableList add a item after list:"+unmodifiableList);
    }

    @Test
    public void testGuavaImmutable(){
        List<String> list=new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        //list：[a, b, c]
        System.out.println("list："+list);

        ImmutableList<String> imlist=ImmutableList.copyOf(list);
        //imlist：[a, b, c]
        System.out.println("imlist："+imlist);

        ImmutableList<String> imOflist=ImmutableList.of("peida","jerry","harry");
        //imOflist：[peida, jerry, harry]
        System.out.println("imOflist："+imOflist);

        ImmutableSortedSet<String> imSortList=ImmutableSortedSet.of("a", "b", "c", "a", "d", "b");
        //imSortList：[a, b, c, d]
        System.out.println("imSortList："+imSortList);

        list.add("baby");
        //list add a item after list:[a, b, c, baby]
        System.out.println("list add a item after list:"+list);
        //list add a item after imlist:[a, b, c]
        System.out.println("list add a item after imlist:"+imlist);

        ImmutableSet<Color> imColorSet =
                ImmutableSet.<Color>builder()
                        .add(new Color(0, 255, 255))
                        .add(new Color(0, 191, 255))
                        .build();
        //imColorSet:[java.awt.Color[r=0,g=255,b=255], java.awt.Color[r=0,g=191,b=255]]
        System.out.println("imColorSet:"+imColorSet);
    }

    @Test
    public void testCotyOf(){
        ImmutableSet<String> imSet=ImmutableSet.of("peida","jerry","harry","lisa");
        System.out.println("imSet："+imSet);
        ImmutableList<String> imlist=ImmutableList.copyOf(imSet);
        System.out.println("imlist："+imlist);
        ImmutableSortedSet<String> imSortSet=ImmutableSortedSet.copyOf(imSet);
        System.out.println("imSortSet："+imSortSet);

        List<String> list=new ArrayList<String>();
        for(int i=0;i<20;i++){
            list.add(i+"x");
        }
        System.out.println("list："+list);
        ImmutableList<String> imInfolist=ImmutableList.copyOf(list.subList(2, 18));
        System.out.println("imInfolist："+imInfolist);
        int imInfolistSize=imInfolist.size();
        System.out.println("imInfolistSize："+imInfolistSize);
        ImmutableSet<String> imInfoSet=ImmutableSet.copyOf(imInfolist.subList(2, imInfolistSize-3));
        System.out.println("imInfoSet："+imInfoSet);
    }
    @Test
    public void testAsList(){
        ImmutableList<String> imList=ImmutableList.of("peida","jerry","harry","lisa","jerry");
        System.out.println("imList："+imList);
        ImmutableSortedSet<String> imSortList=ImmutableSortedSet.copyOf(imList);
        System.out.println("imSortList："+imSortList);
        System.out.println("imSortList as list："+imSortList.asList());
    }

}
