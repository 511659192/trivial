package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/6.
 */
public class TimeMoveTest {

    @Test
    public void testBufferByCount() throws Exception {
        Observable.range(0, 10)
                .buffer(4)
                .subscribe(System.out::println);
    }

    @Test
    public void testBufferByTime() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(10)
//                .timeInterval()
                .buffer(250, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testBufferByCountAndTime() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(10)
                .buffer(250, TimeUnit.MILLISECONDS, 2)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testBufferWithSignal() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(100)
                .buffer(Observable.interval(250, TimeUnit.MILLISECONDS))
//                .concatMap(i -> Observable.from(i))
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    //    当 count > skip 的时候，缓冲的数据重叠了
    //    当 count < skip 的时候，缓冲的数据有丢失
    //    当 count = skip 的时候，和前面看到的简单版本一样
    public void testBufferByCountWithSkip() throws Exception {
        Observable.range(0,10)
                .buffer(4, 5)
                .subscribe(System.out::println);
    }

    @Test
    //    当 timespan > timeshift 的时候，缓冲的数据重叠了
    //    当 timespan < timeshift 的时候，缓冲的数据有丢失
    //    当 timespan = timeshift 的时候，和前面看到的简单版本一样
    public void testBufferByTimeWithSkip() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(10)
                .buffer(350, 200, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testBufferWithSignalAndSkip() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(100)
                .buffer(
                        Observable.interval(250, TimeUnit.MILLISECONDS),
                        i -> {
                            System.out.println(i);
                            return Observable.timer(200, TimeUnit.MILLISECONDS);
                        })
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testTaskLastBufferByCount() throws Exception {
        Observable.range(0, 5)
                .takeLastBuffer(2)
                .subscribe(System.out::println);
    }

    @Test
    public void testTaskLastBufferByTime() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(5)
                .takeLastBuffer(200, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);
        System.in.read();
    }


    @Test
    public void testTaskLastBufferByCountAndTime() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(5)
                .takeLastBuffer(2, 200, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testDelay() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(5)
                .delay(1, TimeUnit.SECONDS)
                .timeInterval()
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testDelayEach() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(5)
                .delay(i -> Observable.timer(i * 100, TimeUnit.MILLISECONDS))
                .timeInterval()
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testDelaySubscription() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(5)
                .delaySubscription(1000, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testDelaySubscriptionWithSignal() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(5)
                .delaySubscription(() -> Observable.timer(1000, TimeUnit.MILLISECONDS))
                .timeInterval()
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testDelayPublisherAndSubscription() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(5)
                .delay(() -> Observable.timer(1000, TimeUnit.MILLISECONDS), i -> Observable.timer(i * 1000, TimeUnit.MILLISECONDS))
                .timeInterval()
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testSample() throws Exception {
        Observable.interval(150, TimeUnit.MILLISECONDS)
                .sample(1, TimeUnit.SECONDS)
                .take(10)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testSampleWithSignal() throws Exception {
        Observable.interval(150, TimeUnit.MILLISECONDS)
                .sample(Observable.interval(1, TimeUnit.SECONDS))
                .take(5)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testThrottleFirst() throws Exception {
        Observable.interval(150, TimeUnit.MILLISECONDS)
                .throttleFirst(1, TimeUnit.SECONDS)
                .take(4)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testThrottleLast() throws Exception {
        Observable.interval(150, TimeUnit.MILLISECONDS)
                .throttleLast(1, TimeUnit.SECONDS)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testDebouncingBase() throws Exception {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                .scan(0, (acc, v) -> acc+1)
//                .timeInterval()
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testDebouncing() throws Exception {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                .scan(0, (acc, v) -> acc+1)
                .debounce(150, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testDebouncingWithSignal() throws Exception {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                .scan(0, (acc, v) -> acc+1)
                .debounce(i -> Observable.timer(i * 50, TimeUnit.MILLISECONDS))
//                .timeInterval()
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testTimeout() throws Exception {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                .scan(0, (acc, v) -> acc+1)
                .timeout(200, TimeUnit.MILLISECONDS)
                .subscribe(
                        System.out::println,
                        System.out::println);
        System.in.read();
    }

    @Test
    public void testTimeoutWithReplacement() throws Exception {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                .scan(0, (acc, v) -> acc+1)
                .timeout(200, TimeUnit.MILLISECONDS, Observable.just(-1))
                .subscribe(
                        System.out::println,
                        System.out::println);
        System.in.read();
    }

    @Test
    public void testTimeoutWithSignal() throws Exception {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                .scan(0, (acc, v) -> acc+1)
                .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS))
                .subscribe(
                        System.out::println,
                        System.out::println);
        System.in.read();
    }
}
