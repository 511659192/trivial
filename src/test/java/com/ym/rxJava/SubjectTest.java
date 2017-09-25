package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/2.
 */
public class SubjectTest {

    /**
     *
     * 结果 2 3 4
     * 数据 1 并没有打印出来，原因是当我们订阅到 subject 的时候，1 已经发射出去了
     */
    @Test
    public void testPublishSubject() {
        PublishSubject<Integer> subject = PublishSubject.create();
        subject.onNext(1);
        subject.subscribe(System.out::println);
        subject.onNext(2);
        subject.subscribe(new PrintSubscriber("subscriber2"));
        subject.onNext(3);
        subject.onNext(4);
    }

    /**
     Read more: http://blog.chengyunfeng.com/?p=948#ixzz4rWcL0qxa
     * 缓存所有发射给他的数据。当一个新的订阅者订阅的时候，缓存的所有数据都会发射给这个订阅者
     * 由于使用了缓存，所以每个订阅者都会收到所以的数据
     */
    @Test
    public void testReplaySubject() {
        ReplaySubject<Integer> s = ReplaySubject.create();
        s.subscribe(v -> System.out.println("Early:" + v));
        for (int i = 0; i < 160; i++) {
            s.onNext(i);
        }
        s.onNext(17);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(18);
    }

    /**
     * 指定只缓存两个数据，所以当订阅的时候第一个数据 0 就收不到了
     */
    @Test
    public void testReplaySubjectWithSize() {
        ReplaySubject<Integer> s = ReplaySubject.createWithSize(2);
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(3);
    }

    @Test
    public void testReplaySubjectWithTime() throws InterruptedException {
        ReplaySubject<Integer> s = ReplaySubject.createWithTime(150, TimeUnit.MILLISECONDS,
                Schedulers.immediate());
        s.onNext(0);
        Thread.sleep(100);
        s.onNext(1);
        Thread.sleep(100);
        s.onNext(2);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(3);
        Thread.sleep(1);
    }

    /**
     * BehaviorSubject 只保留最后一个值。 等同于限制 ReplaySubject 的个数为 1 的情况。在创建的时候可以指定一个初始值
     */
    @Test
    public void testBehaviorSubject() throws InterruptedException {
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.subscribe(new PrintSubscriber("testBehaviorSubject1"));
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(3);


    }

    @Test
    public void testBehaviorSubjectWithDefaultValue() throws InterruptedException {
        BehaviorSubject<Integer> s = BehaviorSubject.create(0);
        s.subscribe(v -> System.out.println(v));
        s.onNext(1);
    }

    @Test
    public void testBehaviorSubjectComplete() {
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.onCompleted();
        s.subscribe(
                v -> System.out.println("Late: " + v),
                e -> System.out.println("Error"),
                () -> System.out.println("Completed")
        );
    }

    /**
     * AsyncSubject 也缓存最后一个数据 只有当数据发送完成时（onCompleted 调用的时候）才发射这个缓存的最后一个数据。
     * 可以使用 AsyncSubject 发射一个数据并立刻结束。
     */
    @Test
    public void testAsyncSubject() throws InterruptedException {
        AsyncSubject<Integer> s = AsyncSubject.create();
        s.subscribe(new PrintSubscriber("testAsyncSubject"));
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        // 不调用 s.onCompleted(); 则什么结果都不会打印出来
        s.onCompleted();
    }

    @Test
    public void testUnsubscribe() throws InterruptedException {
        Subject<Integer, Integer> values = ReplaySubject.create();
        Subscription subscription1 = values.subscribe(
                v -> System.out.println("First: " + v)
        );
        Subscription subscription2 = values.subscribe(
                v -> System.out.println("Second: " + v)
        );
        values.onNext(0);
        values.onNext(1);
        subscription1.unsubscribe();
        System.out.println("Unsubscribed first");
        values.onNext(2);
    }

    @Test
    public void test11() throws Exception {
        BehaviorSubject subject = BehaviorSubject.create();
        subject.subscribe(new PrintSubscriber("test11"));
        Observable.just(1, 2, 3)
                .subscribe(subject);
    }
}
