package com.ym;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * @Author yangmeng44
 * @Date 2017/4/21
 */
public class asynch {

    public static void main(String[] args) throws InterruptedException {
        Observable.from(new Integer[]{1, 2, 3, 4})
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(Schedulers.computation()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer number) {
                        System.out.println(number);
                    }
                });
    }
}
