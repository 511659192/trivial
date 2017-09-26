package com.ym.rxJava;

import rx.Observable;

/**
 * Created by yangm on 2017/9/1.
 */
public interface ICache {
    <T> Observable<T> get(String key, Class<T> cls);

    <T> void put(String key, T t);
}