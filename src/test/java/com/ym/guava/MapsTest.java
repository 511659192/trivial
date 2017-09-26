package com.ym.guava;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangm on 2017/8/27.
 */
public class MapsTest {

    @Test
    public void testMaps() {
        /**
         * difference：返回两个给定map之间的差异。
         */
        Map<String, String> map1 = new HashMap<String, String>() {
            {
                put("a", "1");
            }
        };
        Map<String, String> map2 = new HashMap<String, String>() {
            {
                put("b", "2");
            }
        };
        Map<String, String> map3 = new HashMap<String, String>() {
            {
                put("a", "3");
            }
        };
        //输出：not equal: only on left={a=1}: only on right={b=2}
        System.out.println(Maps.difference(map1, map2));
        //输出：not equal: value differences={a=(1, 3)}
        System.out.println(Maps.difference(map1, map3));
        /**
         * asMap：返回一个活动的map
         * 键值为给定的set中的值
         * value为通过给定Function计算后的值。
         */
        Set<String> set = Sets.newHashSet("a", "b", "c");
        //Function：简单的对元素做大写转换，下面示例多次使用
        Function<String, String> function = new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.toUpperCase();
            }
        };
        //输出：{b=B, c=C, a=A}
        System.out.println(Maps.asMap(set, function));
        /**
         * toMap：返回一个不可变的ImmutableMap实例
         * 其键值为给定keys中去除重复值后的值
         * 其值为键被计算了valueFunction后的值
         */
        List<String> keys = Lists.newArrayList("a", "b", "c", "a");
        //输出：{a=A, b=B, c=C}
        System.out.println(Maps.toMap(keys, function));
        /**
         * uniqueIndex：返回一个不可变的ImmutableMap实例，
         * 其value值为按照给定顺序的给定的values值
         * 键值为相应的值经过给定Function计算后的值
         */
        List<String> values = Lists.newArrayList("a", "b", "c", "d");
        /**
         * 注：这里的value值不可重复，重复的话在转换后会抛出异常：
         * IllegalArgumentException: Multiple entries with same key
         */
        //输出：{A=a, B=b, C=c, D=d}
        System.out.println(Maps.uniqueIndex(values, function));
        /**
         * transformValues：返回一个map映射
         * 其键值为给定fromMap的键值
         * 其value为给定formMap中value通过Function转换后的值
         */
        Map<String, Boolean> fromMap = Maps.newHashMap();
        fromMap.put("key", true);
        fromMap.put("value", false);
        //输出：{value=true, key=false}
        System.out.println(Maps.transformValues(fromMap, new Function<Boolean, Object>() {
            @Override
            public Object apply(Boolean input) {
                //对传入的元素取反
                return !input;
            }
        }));
        /**
         * transformEntries：返回一个map映射
         * 其Entry为给定fromMap.Entry通过给定EntryTransformer转换后的值
         */
        Maps.EntryTransformer<String, Boolean, String> entryTransformer = new Maps.EntryTransformer<String, Boolean, String>() {
            public String transformEntry(String key, Boolean value) {
                //value为假，则key变大写
                return value ? key : key.toUpperCase();
            }
        };
        //输出：{value=VALUE, key=key}
        System.out.println(Maps.transformEntries(fromMap, entryTransformer));
        /**
         * filterKeys：返回给定unfilteredMap中的键值通过给定keyPredicate过滤后的map映射
         */
        //输出：{key=true}
        System.out.println(Maps.filterKeys(fromMap, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                //过滤Map中键值包含字母y的元素
                return input.contains("y");
            }
        }));
        /**
         * filterValues：返回给定unfilteredMap中的value值通过给定keyPredicate过滤后的map映射
         */
        //输出：{value=false}
        System.out.println(Maps.filterValues(fromMap, new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                //过滤Map中value值为假的元素
                return !input;
            }
        }));
        /**
         * filterEntries：返回给定unfilteredMap.Entry中的Entry值通过给定entryPredicate过滤后的map映射
         */
        //输出：{key=true}
        System.out.println(Maps.filterEntries(fromMap, new Predicate<Map.Entry<String, Boolean>>() {
            @Override
            public boolean apply(Map.Entry<String, Boolean> input) {
                //过滤Map.Entry中getValue()为真的元素
                return input.getValue();
            }
        }));
    }

}
