package com.ym.guava;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangm on 2017/8/26.
 */
public class PredicatesTest {

    @Test
    public void testPredicate() {
        Predicate<Girl> predicate = new Predicate<Girl>() {
            @Override
            public boolean apply(Girl input) {
                return input.getAge() >= 18;
            }
        };
        //有个18岁的漂亮姑娘
        Girl girl = new Girl(18, "nice");
        if (predicate.apply(girl)) {
            System.out.println("be my girl friend");
        } else {
            System.out.println("too young to love");
        }
        //输出结果：be my girl friend
    }


    @Test
    public void testPredicates() {
        Predicate<Girl> agePredicate = new Predicate<Girl>() {
            @Override
            public boolean apply(Girl input) {
                return input.getAge() >= 18;
            }
        };
        Predicate<Girl> facePredicate = new Predicate<Girl>() {
            @Override
            public boolean apply(Girl input) {
                return input.getFace().equals("nice");
            }
        };
        Girl girl = new Girl(18, "ugly");

        //and：用于过滤两个Predicate都为true
        Predicate<Girl> predicate = Predicates.and(agePredicate, facePredicate);
        checkOut(predicate.apply(girl)); //输出结果：She doesn't fit me

        //or：用于过滤其中一个Predicate为true
        predicate = Predicates.or(agePredicate, facePredicate);
        checkOut(predicate.apply(girl));  //输出结果：She fits me

        //or：用于将指定Predicate取反
        Predicate<Girl> noneAgePredicate = Predicates.not(agePredicate);
        predicate = Predicates.and(noneAgePredicate, facePredicate);
        checkOut(predicate.apply(girl));  //输出结果：She doesn't fit me

        //compose: Function与Predicate的组合
        Map<String, Girl> map = new HashMap<String, Girl>() {
            //构造一个测试用Map集合
            {
                put("love", new Girl(18, "nice"));
                put("miss", new Girl(16, "ugly"));
            }
        };
        Predicate<Girl> predicate1 = Predicates.and(agePredicate, facePredicate);
        Function<String, Girl> function1 = Functions.forMap(map);
        Predicate<String> stringPredicate = Predicates.compose(predicate1, function1);
        System.out.println(stringPredicate.apply("love"));//true
        System.out.println(stringPredicate.apply("miss"));//false
    }

    /**
     * 判断输出
     *
     * @param flag
     */
    private void checkOut(boolean flag) {
        if (flag) {
            System.out.println("She fits me");
        } else {
            System.out.println("She doesn't fit me");
        }
    }
    class Girl {
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
}

