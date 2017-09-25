package com.ym.guava;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by yangm on 2017/8/27.
 */
public class FluentIterableTest {

    //测试用Girl对象
    static class Girl {
        int age;
        String face;

        Girl(int age, String face) {
            this.age = age;
            this.face = face;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getFace() {
            return face;
        }

        public void setFace(String face) {
            this.face = face;
        }
    }

    private List<Girl> girls;//测试用对象集合

    //这里初始化一些测试数据
    @Before
    public void setUp() {
        Girl girl1 = new Girl(17, "nice");
        Girl girl2 = new Girl(18, "not so nice");
        Girl girl3 = new Girl(19, "nice");
        Girl girl4 = new Girl(20, "not so nice");
        //这里使用Lists.newArrayList添加对象，避免多次调用list.add方法，下篇会有说明
        girls = Lists.newArrayList(girl1, girl2, girl3, girl4);
    }

    @Test
    public void testFluentIterable() {
        /**
         * from方法：用于包装Iterable接口，返回FluentIterable实例
         * filter方法：用于过滤集合中元素，返回过滤后的集合
         */
        Iterable<Girl> iterable = FluentIterable.from(girls).filter(new Predicate<Girl>() {
            @Override
            public boolean apply(Girl input) {
                //过滤相貌nice的Girl对象
                return "nice".equals(input.getFace());
            }
        });
        for (Girl girl : iterable) {
            /**打印结果：
             17
             19
             */
            System.out.println(girl.getAge());
        }
        /**
         * transform方法：用于根据指定Function转换集合元素，返回转换后的集合
         */
        Iterable<String> fluentIterable = FluentIterable.from(girls).transform(new Function<Girl, String>() {
            @Override
            public String apply(Girl input) {
                //Joiner类对相应的元素内容进行拼接处理
                return Joiner.on("，").join(input.getAge(), input.getFace());
            }
        });
        for (String girl : fluentIterable) {
            /** 打印结果：
             17，nice
             18，not so nice
             19，nice
             20，not so nice
             */
            System.out.println(girl);
        }
    }

}
