package com.ym.guava;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangm on 2017/8/26.
 */
public class FunctionsTest {

    @Test
    public void testFunctions() {
        Map<String, Integer> map = new HashMap<String, Integer>() {
            //构造一个测试用Map集合
            {
                put("love", 2);
                put("miss", 3);
            }
        };
        /**
         * forMap
         */
        Function<String, Integer> function = Functions.forMap(map);
        //调用apply方法，可以通过key获取相应的value
        System.out.println(function.apply("love"));//i love u
        //当apply的key值在Map里不存在时，会抛出异常
        //java.lang.IllegalArgumentException: Key 'like' not present in map
//        System.out.println(function.apply("like"));
        //我们可以通过forMap重载的另一个方法避免异常，当Key不存在时，设置一个默认值
        function = Functions.forMap(map, 0);
        System.out.println(function.apply("like"));//can't find this key
        /**
         * 有时候，我们需要多个Function进行组合，
         * 这时就需要用到compose，如下：
         */
        //我们有一个Function用于将输入的数字进行平方运算
        Function<Integer, Integer> function1 = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input * input;
            }
        };
        //我们将上面Map中的value值全部进行平方运算
        /**
         * Warning：这里compose方法的参数位置不能颠倒，
         * Function<A, C> compose(Function<B, C> g, Function<A, ? extends B> f)
         * 传入Function<B,C>、Function<A, ? extends B>组合成Function<A, C>
         */
        Function<String, Integer> result = Functions.compose(function1, function);
        System.out.println(result.apply("love"));//I LOVE U
        //当Key值不存在时，结果也是大写的
        System.out.println(result.apply("like"));//CAN'T FIND THIS KEY
    }
}
