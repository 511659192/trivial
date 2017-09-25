package com.ym.rxJava;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by yangm on 2017/9/1.
 */
public class CacheLoader {

    private ICache mMemoryCache, mDiskCache;

    private CacheLoader() {

        mMemoryCache = new MemoryCache();
        mDiskCache = new DiskCache();
    }
    private static CacheLoader loader;

    public static CacheLoader getInstance() {
        if (loader == null) {
            synchronized (CacheLoader.class) {
                if (loader == null) {
                    loader = new CacheLoader();
                }
            }
        }
        return loader;
    }


    public <T> Observable<T> asDataObservable(String key, Class<T> cls) {

        Observable observable = Observable.concat(
                memory(key, cls),
                disk(key, cls))
                .firstOrDefault(null, new Func1<T, Boolean>() {
                    @Override
                    public Boolean call(T t) {
                        return t != null;
                    }
                });
        return observable;
    }

    private <T> Observable<T> memory(String key, Class<T> cls) {

        return mMemoryCache.get(key, cls).doOnNext(new Action1<T>() {
            @Override
            public void call(T t) {
                if (null != t) {
                    System.out.println("我是来自内存");
                }
            }
        });
    }

    private <T> Observable<T> disk(final String key, Class<T> cls) {

        return mDiskCache.get(key, cls)
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        if (null != t) {
                            System.out.println("我是来自磁盘"  + t);
                            mMemoryCache.put(key, t);
                        }
                    }
                });
    }


    public void clearMemory(String key) {
        ((MemoryCache)mMemoryCache).clearMemory(key);
    }



    public void clearMemoryDisk(String key) {
        ((MemoryCache)mMemoryCache).clearMemory(key);
        ((DiskCache)mDiskCache).clearDisk(key);
    }

    public static void main(String[] args) {
        getInstance().asDataObservable("a", String.class)
                .subscribe(System.out::println);
    }
}

