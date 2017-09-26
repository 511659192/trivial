package com.ym.guava;

import com.google.common.base.Splitter;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Created by yangm on 2017/8/26.
 */
public class SplitterTest {

    private static String separator = "|";// 分隔符
    private static String separator2 = "=";// 分隔符（mapSplitter）

    private static String string = "i hate u=u hate me |i like u=u like me |||i love u=u love me";// 测试用字符串

    @Test
    public void testSplitter() {
        // omitEmptyStrings方法用于忽略分割后产生的空值
        Iterable strings = Splitter.on(separator).omitEmptyStrings()
                .split(string);
        // [i hate u=u hate me , i like u=u like me , i love u=u love me]
        System.out.println(strings);
        // trimResults方法用于处理分割后每一项中的空白符
        Iterable strings2 = Splitter.on(separator)
                .trimResults().split(string);
        // [i hate u=u hate me, i like u=u like me, , , i love u=u love me]
        System.out.println(strings2);
    }

    @Test
    public void testMapSplitter() {
        Splitter.MapSplitter mapSplitter = Splitter.on(separator)
                .omitEmptyStrings().withKeyValueSeparator(separator2);
        Map<String, String> map = mapSplitter.split(string);
        Set keySet = map.keySet();
        // [i hate u, i like u, i love u]
        System.out.println(keySet);
    }

}
