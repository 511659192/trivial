package com.ym.guava;

import com.google.common.base.Joiner;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangm on 2017/8/26.
 */
public class JoinerTest {

    private static String separator = "|";//分隔符

    private static List<String> list = initTestingData();//测试用List集合

    //哈哈，这里炫一下，初始化一个map集合，用于测试
    private static Map<String,String> map = new HashMap<String,String>(){

        private static final long serialVersionUID = 1L;
        {
            put("i love u", "u love me");
            put("i like u", "u like me");
            put("i hate u", "u hate me");
        }
    };

    /**
     * 初始化测试数据
     *
     * @return
     */
    private static List initTestingData() {
        List list = new ArrayList();
        for (int i = 0; i < 20; i++) {
            if(i%5==0){//方便测试,设置null值
                list.add(null);
            }else{
                list.add("test" + i);
            }
        }
        return list;
    }

    /**
     * 传统方法通过循环处理字符串集合
     */
    @Test
    public void testBuildStringWithLoop(){
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (s != null) {
                sb.append(s).append(separator);
            }
        }
        sb.setLength(sb.length() - separator.length());
        System.out.println(sb.toString());
    }

    /**
     * 通过guava库提供的Joiner类处理字符串集合
     */
    @Test
    public void testBuildStringWithJoiner(){
        //skipNulls用于过滤集合中的null值
        String str = Joiner.on(separator).skipNulls().join(list);
        System.out.println(str);
        //useForNull用于替换集合中的null值
        String str2 = Joiner.on(separator).useForNull("hello").join(list);
        System.out.println(str2);
    }

    /**
     * 使用Joiner处理StringBuilder
     */
    @Test
    public void testJoinerStringBuilder(){
        StringBuilder sb = new StringBuilder();
        Joiner joiner = Joiner.on(separator).skipNulls();
        //使用appendTo方法接收StringBuilder实例,并返回处理过的StringBuilder
        joiner.appendTo(sb, "i love u","i like u","i hate u");
        System.out.println(sb.toString());
    }

    /**
     *  与Joiner处理list类似，使用MapJoiner专门处理map集合，
     *  withKeyValueSeparator用于接收key和value的分隔符
     */
    @Test
    public void testMapJoiner() {
        String str = Joiner.on(separator).withKeyValueSeparator("=").join(map);
        System.out.println(str);
    }

}