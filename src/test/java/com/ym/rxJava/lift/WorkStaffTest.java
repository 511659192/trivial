package com.ym.rxJava.lift;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by cdyangmeng on 2017/10/16.
 */
public class WorkStaffTest {

    @Test
    public void testCas() throws Exception {
        Map<String, String> map1 = Maps.newHashMap();

        Map<String, String> map2 = Maps.newHashMap();

        AtomicReference<Map<String, String>> base = new AtomicReference<>(map1);

        System.out.println(base.compareAndSet(map1, map2));

        map2.put("a", "a");

        System.out.println(map2);

        System.out.println(base.compareAndSet(map1, map2));
        System.out.println(base.compareAndSet(map2, map1));

    }
}
