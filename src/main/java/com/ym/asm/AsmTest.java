package com.ym.asm;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AsmTest {

    public Integer set(Integer i, String bb, List<Integer> list, Set<Integer> set, Map<Integer, Integer> map, Object object) {
        i++;
        String cc = bb;
        for (Integer integer : list) {
        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {

        }

        if (object != null)
            cc = object.toString();
        return 1;
    }
}
