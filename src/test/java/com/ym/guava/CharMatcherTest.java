package com.ym.guava;

import com.google.common.base.CharMatcher;
import org.junit.Test;

/**
 * Created by yangm on 2017/8/26.
 */
public class CharMatcherTest {

    private static String sequence = "HELLO   RealFighter ~!@#$%^&*() ，,.。   \n123(*&gS   你好\t234啊   abc  \n";

    //打印方法
    private static void print(Object obj) {
        System.out.println(String.valueOf(obj));
    }

    @Test
    public void testCharMatcher() {
        //原始字符串
        print(sequence);
        //使用JAVA_ISO_CONTROL去除所有控制字符\n\t
        String str = CharMatcher.JAVA_ISO_CONTROL.removeFrom(sequence);
        print(str);
        //筛选出所有的字母(包含中文)或数字
        str = CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(sequence);
        print(str);
        //匹配sequence中的数字并全部替换成*号
        str = CharMatcher.JAVA_DIGIT.replaceFrom(sequence,"*");
        print(str);
        //去除首尾连续匹配到的大写字符
        str = CharMatcher.JAVA_UPPER_CASE.trimFrom(sequence);
        print(str);
        //去除首部连续匹配到的大写字符
        str = CharMatcher.JAVA_UPPER_CASE.trimLeadingFrom(sequence);
        print(str);
        //去除尾部连续匹配到的大写字符
        str = CharMatcher.JAVA_UPPER_CASE.trimTrailingFrom(sequence);
        print(str);
        //将匹配到的大写字符替换成问号
        str = CharMatcher.JAVA_UPPER_CASE.collapseFrom(sequence,'?');
        print(str);
        //去除首尾空白符后将匹配到的小写字符替换为问号
        str = CharMatcher.JAVA_LOWER_CASE.trimAndCollapseFrom(sequence,'?');
        print(str);
    }

}
