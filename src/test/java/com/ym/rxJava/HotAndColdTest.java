package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/6.
 */
public class HotAndColdTest {

    @Test
    public void testCold() throws Exception {
        Observable<Long> cold = Observable.interval(1, TimeUnit.MILLISECONDS).take(5);

        cold.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(5000);
        cold.subscribe(i -> System.out.println("Second: " + i));
        System.in.read();
    }

    @Test
    public void testConnect() throws Exception {
        ConnectableObservable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS)
                .take(5)
                .publish();
        cold.connect();

        cold.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(500);
        cold.subscribe(i -> System.out.println("Second: " + i));
        System.in.read();
    }

    @Test
    public void testDisconnecting() throws Exception {
        ConnectableObservable<Long> connectable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
        Subscription s = connectable.connect();

        connectable.subscribe(i -> System.out.println(i));

        Thread.sleep(1000);
        System.out.println("Closing connection");
        s.unsubscribe();

        Thread.sleep(1000);
        System.out.println("Reconnecting");
        s = connectable.connect();
//        connectable.subscribe(i -> System.out.println(i));
        System.in.read();
    }

    @Test
    public void testDisconnecting2() throws Exception {
        ConnectableObservable<Long> connectable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
        Subscription s = connectable.connect();

        Subscription s1 = connectable.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(500);
        Subscription s2 = connectable.subscribe(i -> System.out.println("Second: " + i));

        Thread.sleep(500);
        System.out.println("Unsubscribing second");
        s2.unsubscribe();
        System.in.read();
    }

    @Test
    public void testRefCount() throws Exception {
        Observable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).publish().refCount();

        Subscription s1 = cold.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(500);
        Subscription s2 = cold.subscribe(i -> System.out.println("Second: " + i));
        Thread.sleep(500);
        System.out.println("Unsubscribe second");
        s2.unsubscribe();
        Thread.sleep(500);
        System.out.println("Unsubscribe first");
        s1.unsubscribe();

        System.out.println("First connection again");
        Thread.sleep(500);
        s1 = cold.subscribe(i -> System.out.println("First: " + i));
        System.in.read();
    }

    @Test
    public void testReplay() throws Exception {
        ConnectableObservable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).replay();
        Subscription s = cold.connect();

        System.out.println("Subscribe first");
        Subscription s1 = cold.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(700);
        System.out.println("Subscribe second");
        Subscription s2 = cold.subscribe(i -> System.out.println("Second: " + i));
        Thread.sleep(500);
        System.in.read();
    }

    @Test
    public void testCache() throws Exception {
        Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(5)
                .cache();

        Thread.sleep(500);
        obs.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(300);
        obs.subscribe(i -> System.out.println("Second: " + i));
        System.in.read();
    }

    @Test
    public void testCacheWithUnsubscribe() throws Exception {
        Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(5)
                .doOnNext(System.out::println)
                .cache() // 所有的订阅者都取消订阅了 内部的 ConnectableObservable 不会取消订阅
                .doOnSubscribe(() -> System.out.println("Subscribed"))
                .doOnUnsubscribe(() -> System.out.println("Unsubscribed"));

        Subscription subscription = obs.subscribe();
        Thread.sleep(150);
        subscription.unsubscribe();
        System.in.read();
    }
}
