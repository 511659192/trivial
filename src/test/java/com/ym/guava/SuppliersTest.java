package com.ym.guava;

import com.google.common.base.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/8/26.
 */
public class SuppliersTest {


    @Test
    public void testSupplier() {
        Supplier<Predicate<String>> supplier = new Supplier<Predicate<String>>() {
            @Override
            public Predicate<String> get() {
                Map<String, Girl> map = new HashMap<String, Girl>() {
                    {
                        put("love the age", new Girl(18, "not so nice"));
                        put("love the face", new Girl(16, "so nice"));
                    }
                };
                Function<String, Girl> function = Functions.forMap(map);
                Predicate<Girl> predicate = new Predicate<Girl>() {
                    @Override
                    public boolean apply(Girl input) {
                        return input.getAge() >= 18;
                    }
                };
                Predicate<String> result = Predicates.compose(predicate, function);
                return result;
            }
        };
        System.out.println(supplier.get().apply("love the age"));//true
        System.out.println(supplier.get().apply("love the face"));//false
    }

    @Test
    public void testSuppliers() {
        Supplier<Predicate<String>> supplier = new Supplier<Predicate<String>>() {
            @Override
            public Predicate<String> get() {
                Map<String, Girl> map = new HashMap<String, Girl>() {
                    {
                        put("love the age", new Girl(18, "not so nice"));
                        put("love the face", new Girl(16, "so nice"));
                    }
                };
                Function<String, Girl> function = Functions.forMap(map);
                Predicate<Girl> predicate = new Predicate<Girl>() {
                    @Override
                    public boolean apply(Girl input) {
                        return input.getAge() >= 18;
                    }
                };
                Predicate<String> result = Predicates.compose(predicate, function);
                return result;
            }
        };
        //Supplier.memoize方法，返回传入参数Supplier的包装类，
        //当get()方法第一次被调用，Supplier的包裹被创建，
        //包装类缓存了Supplier实例，并将其返回给调用者
        Supplier<Predicate<String>> wrapped = Suppliers.memoize(supplier);
        System.out.println(wrapped.get().apply("love the age"));//true
        //Supplier.memoizeWithExpiration方法，设定时间的数值（10l）和单位(TimeUnit.SECONDS)
        // 返回传入参数Supplier的包装类，当get方法被调用，在指定的时间内，
        // memoizeWithExpiration作用与memoize相同，包装类缓存Supplier实例给定的时间
        Supplier<Predicate<String>> wrapped2 = Suppliers
                .memoizeWithExpiration(supplier, 10l, TimeUnit.SECONDS);
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

