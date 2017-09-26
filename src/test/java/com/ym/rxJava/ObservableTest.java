package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;

import java.io.IOException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/2.
 */
public class ObservableTest {

    @Test
    public void testJust() {
        Observable<String> values = Observable.just("one", "two", "three");
        Subscription subscription = values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
    }

    @Test
    public void testEmpty() {
        Observable<String> values = Observable.empty();
        Subscription subscription = values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
    }

    @Test
    public void testNever() {
        Observable<String> values = Observable.never();
        Subscription subscription = values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
    }

    @Test
    public void testError() {
        Observable<String> values = Observable.error(new Exception("Oops"));
        Subscription subscription = values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
    }

    /**
     * defer 并没有定义一个新的 Observable， defer 只是用来声明当 Subscriber 订阅到一个 Observable 上时，该 Observable 应该如何创建
     */
    @Test
    public void testDefer() throws InterruptedException {
        Observable<Long> now = Observable.just(System.currentTimeMillis());
        now.subscribe(System.out::println);
        Thread.sleep(1000);
        now.subscribe(System.out::println);
        // 注意上面两个 subscriber 相隔 1秒订阅这个 Observable，但是他们收到的时间数据是一样的！这是因为当订阅的时候，时间数据只调用一次

        Observable<Long> now2 = Observable.defer(() ->
                Observable.just(System.currentTimeMillis()));
        now2.subscribe(System.out::println);
        Thread.sleep(1000);
        now2.subscribe(System.out::println);
        // defer 的参数是一个返回一个 Observable 对象的函数。该函数返回的 Observable 对象就是 defer 返回的 Observable 对象。
        // 重点是，每当一个新的 Subscriber 订阅的时候，这个函数就重新执行一次。
    }

    @Test
    public void testCreate() throws Exception {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Hello");
            o.onCompleted();
        });
        Subscription subscription = values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
    }

    @Test
    public void testRanger() throws InterruptedException {
        Observable<Integer> values = Observable.range(10, 15);
        values.subscribe(System.out::println);
    }

    @Test
    public void testInterval() throws InterruptedException, IOException {
        Observable<Long> values = Observable.interval(1000, TimeUnit.MILLISECONDS);
        Subscription subscription = values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
        System.in.read();
    }

    @Test
    public void testTimer() throws InterruptedException, IOException {
        Observable<Long> values = Observable.timer(1, TimeUnit.SECONDS);
        Subscription subscription = values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        Observable<Long> values2 = Observable.timer(2, 1, TimeUnit.SECONDS);
        Subscription subscription2 = values2.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
        System.in.read();
    }

    @Test
    public void testFrom() throws InterruptedException {
        FutureTask<Integer> f = new FutureTask<Integer>(() -> {
            Thread.sleep(2000);
            return 21;
        });
        new Thread(f).start();

//        Observable<Integer> values = Observable.from(f);
        Observable<Integer> values = Observable.from(f, 1000, TimeUnit.MILLISECONDS);

        Subscription subscription = values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );


        Integer[] is = {1,2,3};
        Observable<Integer> values2 = Observable.from(is);
        Subscription subscription2 = values2.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
    }
}
