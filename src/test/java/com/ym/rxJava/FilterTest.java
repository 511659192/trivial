package com.ym.rxJava;

import com.sun.org.apache.xpath.internal.axes.SelfIteratorNoPredicate;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/3.
 */
public class FilterTest {

    @Test
    public void testFilter() {
        Observable<Integer> values = Observable.range(0,10);
        Subscription oddNumbers = values
                .filter(v -> v % 2 == 0)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testDistinct() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onNext(2);
            o.onCompleted();
        });

        Subscription subscription = values
                .distinct()
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testDistinctWithKeySelector() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("First");
            o.onNext("Second");
            o.onNext("Third");
            o.onNext("Fourth");
            o.onNext("Fifth");
            o.onCompleted();
        });

        Subscription subscription = values
                .distinct(v -> v.charAt(0))
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testDistinctUntilChanged() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onNext(2);
            o.onCompleted();
        });

        Subscription subscription = values
                .distinctUntilChanged()
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testDistinctUntilChangedWithKeySelector() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("First");
            o.onNext("Second");
            o.onNext("Third");
            o.onNext("Fourth");
            o.onNext("Fifth");
            o.onCompleted();
        });

        Subscription subscription = values
                .distinctUntilChanged(v -> v.charAt(0))
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testIgnoreElements() {
        Observable<Integer> values = Observable.range(0, 10);

        Subscription subscription = values
                .ignoreElements() // ignoreElements 会忽略所有发射的数据，只让 onCompleted 和 onError 可以通过
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }


    @Test
    public void testTask() throws IOException {
//        Observable<Integer> values = Observable.range(0, 5);
//
//        Subscription first2 = values
//                .take(2)
//                .subscribe(
//                        v -> System.out.println(v),
//                        e -> System.out.println("Error: " + e),
//                        () -> System.out.println("Completed")
//                );

        Observable<Integer> values2 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> o) {
                o.onNext(1);
                o.onError(new Exception("Oops"));
            }
        });

        Subscription subscription2 = values2
                .take(2)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );

        Observable<Long> values3 = Observable.interval(100, TimeUnit.MILLISECONDS);

        Subscription subscription3 = values3
                .take(250, TimeUnit.MILLISECONDS) // 只获取前 250 毫秒发射的数据。 第 300 毫秒才开始发射数据 3， 所以这里只获取 0 和1 两个数据。
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
        System.in.read();
    }

    @Test
    public void testSkip() {
        Observable<Integer> values = Observable.range(0, 5);

        Subscription subscription = values
                .skip(2)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testTakeWhile() throws IOException {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);
        values = Observable.just(1L, 2L, 3L, 4L);


        Subscription subscription = values
                .takeWhile(v -> v % 2 == 0)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
        System.in.read();
    }

    @Test
    public void testSkipWhile() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        Subscription subscription = values
                .skipWhile(v -> v < 2)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );

        System.in.read();
    }

    @Test
    public void testSkipLast() throws Exception {
        Observable<Integer> values = Observable.range(0,5);

        Subscription subscription = values
                .skipLast(2)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testTaskUntil() throws Exception {

        Observable<Long> value = Observable.interval(100, TimeUnit.MILLISECONDS);

        Subscription subscription2 = value
                .takeUntil(v -> v < 2) // 有一项为true即刻终止
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
        System.in.read();
    }

    @Test
    public void testTaskUntilWithOtherObservable() throws Exception {
        Observable<Long> values = Observable.interval(100,TimeUnit.MILLISECONDS);
        // 切断的条件为 另外一个 Observable 发射数据的时刻。
        // cutoff 这个充当信号的 Observable 可以是任意数据类型的，这里不关心数据只关心何时发射了数据。
        Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS);
        values = Observable.just(1L, 2L, 3L);
        cutoff = Observable.just(2L);

        Subscription subscription = values
                .takeUntil(cutoff)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );

        System.in.read();
    }
}

