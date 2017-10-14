package com.ym.rxJava.user;

import java.util.List;

/**
 * Created by cdyangmeng on 2017/10/12.
 */
public class Pair<A, B> {

    public A first;
    public B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair create(A a, B b) {
        return new Pair(a, b);
    }
}
