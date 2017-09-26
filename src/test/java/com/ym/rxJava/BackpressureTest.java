package com.ym.rxJava;


import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/9.
 */
public class BackpressureTest {

    @Test
    public void testSample() throws Exception {
        Observable.interval(1, TimeUnit.MILLISECONDS)
//                .observeOn(Schedulers.newThread())
                .sample(100, TimeUnit.MILLISECONDS)
                .subscribe(
                        i -> {
                            System.out.println(i);
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) { }
                        },
                        System.out::println);
        System.in.read();
    }

    @Test
    public void testCollect() throws Exception {
        Observable.interval(10, TimeUnit.MILLISECONDS)
//                .observeOn(Schedulers.newThread())
                .buffer(100, TimeUnit.MILLISECONDS)
                .subscribe(
                        i -> {
                            System.out.println(i);
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) { }
                        },
                        System.out::println);
        System.in.read();
    }

    class MySubscriber extends Subscriber<T> {
        @Override
        public void onStart() {
            request(1);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(T n) {
            request(1);
        }
    }

    @Test
    public void testDoOnRequest() throws Exception {
        Observable.range(0, 3)
                .doOnRequest(i -> System.out.println("Requested " + i))
                .subscribe(System.out::println);

    }


    class ControlledPullSubscriber<T> extends Subscriber<T> {

        private final Action1<T> onNextAction;
        private final Action1<Throwable> onErrorAction;
        private final Action0 onCompletedAction;

        public ControlledPullSubscriber(
                Action1<T> onNextAction,
                Action1<Throwable> onErrorAction,
                Action0 onCompletedAction) {
            this.onNextAction = onNextAction;
            this.onErrorAction = onErrorAction;
            this.onCompletedAction = onCompletedAction;
        }

        public ControlledPullSubscriber(
                Action1<T> onNextAction,
                Action1<Throwable> onErrorAction) {
            this(onNextAction, onErrorAction, () -> {});
        }

        public ControlledPullSubscriber(Action1<T> onNextAction) {
            this(onNextAction, e -> {}, () -> {});
        }

        @Override
        public void onStart() {
            request(0);
        }

        @Override
        public void onCompleted() {
            onCompletedAction.call();
        }

        @Override
        public void onError(Throwable e) {
            onErrorAction.call(e);
        }

        @Override
        public void onNext(T t) {
            onNextAction.call(t);
        }

        public void requestMore(int n) {
            request(n);
        }
    }

    @Test
    public void testRequestMore() throws Exception {
        ControlledPullSubscriber<Integer> puller =
                new ControlledPullSubscriber<Integer>(System.out::println);

        Observable.range(0, 3)
//                .doOnRequest(i -> System.out.println("Requested " + i))
                .subscribe(puller);

        puller.requestMore(2);
        puller.requestMore(1);
    }

    @Test
    public void testZipWith() throws Exception {
        Observable.range(0, 300)
                .doOnRequest(i -> System.out.println("Requested " + i))
                .zipWith(
                        Observable.range(10, 300),
                        (i1, i2) -> i1 + " - " + i2)
                .take(300)
                .subscribe(System.out::println);
    }

    @Test
    public void testOnBackpressureBuffer() throws Exception {
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .onBackpressureBuffer(1000)
                .observeOn(Schedulers.newThread())
                .subscribe(
                        i -> {
                            System.out.println(i);
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) { }
                        },
                        System.out::println
                );
        System.in.read();
    }

    @Test
    public void testOnBackpressureDrop() throws Exception {
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .observeOn(Schedulers.newThread())
                .subscribe(
                        i -> {
                            System.out.println(i);
                            try {
                                Thread.sleep(10);
                            } catch (Exception e) { }
                        },
                        System.out::println);
        System.in.read();
    }
}
