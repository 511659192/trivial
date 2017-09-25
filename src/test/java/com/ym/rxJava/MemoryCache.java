package com.ym.rxJava;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.JSON;
import org.apache.http.util.TextUtils;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by yangm on 2017/9/1.
 */
public class MemoryCache implements ICache{

    private LRUCache<String, String> mCache;

    public MemoryCache() {
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        final int cacheSize = maxMemory / 8;
        mCache = new LRUCache<String, String>(cacheSize);
    }

    @Override
    public <T> Observable<T> get(final String key, final Class<T> cls) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                String result = mCache.get(key);

                if (subscriber.isUnsubscribed()) {
                    return;
                }

                if (TextUtils.isEmpty(result)) {
                    subscriber.onNext(null);
                } else {
                    T t = JSON.parseObject(result, cls);
                    subscriber.onNext(t);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public <T> void put(String key, T t) {
        if (null != t) {
            mCache.put(key, JSON.toJSONString(t));
        }
    }

    public void clearMemory(String key) {
        mCache.remove(key);
    }
}