package com.ym.rxJava;

import com.alibaba.fastjson.JSON;
import rx.Subscriber;

/**
 * Created by yangm on 2017/9/3.
 */
public class PrintSubscriber extends Subscriber {
    private final String name;
    public PrintSubscriber(String name) {
        this.name = name;
    }
    @Override
    public void onCompleted() {
        System.out.println(name + ": Completed");
    }
    @Override
    public void onError(Throwable e) {
//        e.printStackTrace();
        System.out.println(name + ": Error: " + e);
    }
    @Override
    public void onNext(Object v) {
        System.out.println(name + ": " + JSON.toJSONString(v));
    }
}

