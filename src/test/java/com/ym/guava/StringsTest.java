package com.ym.guava;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * Created by yangm on 2017/8/26.
 */
public class StringsTest {

    private static final String string = "test";//测试用字符串
    /**
     * 输出方法
     * @param obj
     */
    private void print(Object obj) {
        System.out.println(String.valueOf(obj));
    }

    /**
     * Charsets是一个字符集常量类，包装了常用的字符集常量
     */
    @Test
    public void testCharsets() {
        print(Charsets.UTF_8);
        print(Charsets.ISO_8859_1);
        print(Charsets.US_ASCII);
        print(Charsets.UTF_16);
        print(Charsets.UTF_16BE);
        print(Charsets.UTF_16LE);
        byte[] bytes;
        try {
            bytes = string.getBytes("UTF-8");
            //如果手动指定编码，这里可能会有一个不支持编码的异常
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //使用string.getByters(Charsets.UTF_8)避免了此异常
        //不过没有GBK编码，可能是guava开发组的人忘记了
        bytes = string.getBytes(Charsets.UTF_8);
    }

    /**
     * Strings：字符串常用处理方法
     */
    @Test
    public void testStrings() {
        //我们需要将测试字符串变成一个长度为6的字符串，位数不够的话填充字符c
        //这样的需求，经常出现在有金额表示的时候，如4.0补充到4.000
        //传统的做法，我们需要循环追加，如下
        StringBuilder sb = new StringBuilder(string);
        char c = 's';
        for (int i = 0; i < 6 - string.length(); i++) {
            sb.append(c);
        }
        print(sb.toString());//testss

        //guava中提供了padEnd（param1,length,param2）方法
        //将param1的长度改变为length，长度不足的话向后追加字符param2
        print(Strings.padEnd(string, 6, c));//testss
        //这里如果param1的长度大于length，则默认返回原字符串param1
        print(Strings.padEnd(string, 2, c));//test
        //与之类似，也有padStart()方法，向前追加，长度超出默认返回原字符
        print(Strings.padStart(string, 6, c));//sstest

        //另外，还有几个常用的方法
        //nullToEmpty：将null值转为空字符串
        print(Strings.nullToEmpty(null));//""
        //emptyToNull：将空字符串转为null
        print(Strings.emptyToNull(""));//null
        //isNullOrEmpty：判断字符串为null或空字符串
        print(Strings.isNullOrEmpty(null));//true
        //repeat：用于将指定字符串循环拼接多次返回
        print(Strings.repeat(string,3));//testtesttest

        //另外，有两个方法用来进行字符串的比较
        //commonSuffix：返回两个字符串中相同的后缀部分
        print(Strings.commonSuffix("nihaoma?","nibuhaoma?")); //haoma?
        //commonPrefix：返回两个字符串中相同的前缀部分
        print(Strings.commonPrefix("nihaoma?","nibuhaoma?")); //ni

    }

}
