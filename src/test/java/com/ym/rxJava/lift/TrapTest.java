package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.Subscribers;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.TimeUnit;

/**
 * Created by cdyangmeng on 2017/10/1.
 */
public class TrapTest {

    @Test
    public void testIsOdd() {
        Observable.Operator<Boolean, Integer> isOdd = child -> {
//            return new Subscriber<Integer>() {                    // (1)
            return new Subscriber<Integer>(child) {                    // (1)
                @Override
                public void onNext(Integer value) {
                    child.onNext((value & 1) != 0);
                }
                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }
                @Override
                public void onCompleted() {
                    child.onCompleted();
                }
            };
        };

        Observable.range(1, 2_000_000_000)
                .lift(isOdd)
                .take(2)
                .subscribe(System.out::println);
    }

    @Test
    public void testTakeNone() {
        Observable.Operator<Integer, Integer> takeNone = child -> {
            return new Subscriber<Integer>(child, false) {          // (1) 只会取消自己以及上游，下游并不会被取消订阅
//            return new Subscriber<Integer>(child) {          // (1)
                @Override
                public void onNext(Integer t) {
                    child.onCompleted();
                    unsubscribe();                           // (2)
                }

                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }

                @Override
                public void onCompleted() {
                    child.onCompleted();
                }
            };
        };

        TestSubscriber<Integer> ts = new TestSubscriber<>();

        Subscription importantResource = Subscriptions.empty();
        ts.add(importantResource);

        Observable.range(1, 100).lift(takeNone).unsafeSubscribe(ts);

        if (importantResource.isUnsubscribed()) {
            System.err.println("Somebody unsubscribed our resource!");
        }
    }

    @Test
    public void testRequestMore() {
        Observable.Operator<Integer, Integer> evenFilter = child -> {
            return new Subscriber<Integer>(child) {
                @Override
                public void onNext(Integer t) {
                    if ((t & 1) == 0) {
                        child.onNext(t);
                    } else {
                        // (1)
                        request(1);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }

                @Override
                public void onCompleted() {
                    child.onCompleted();
                }
            };
        };

//        Observable.range(1, 2).lift(evenFilter).subscribe(
//                System.out::println,
//                Throwable::printStackTrace,
//                () -> System.out.println("Done"));

        Observable.range(1, 2).lift(evenFilter).take(1)
                .subscribe(
                System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done"));
    }

    @Test
    public void testOther() throws InterruptedException {
        Observable<Integer> other = Observable.<Integer>create(o -> {
            try {
                Thread.sleep(100);
            } catch (Throwable e) {
                o.onError(e);
                return;
            }
            o.onNext(0);
            o.onCompleted();
        }).subscribeOn(Schedulers.io());

        Observable.Operator<Integer, Integer> takeUntilZero = child -> {
            Subscriber<Integer> main = new Subscriber<Integer>(child, false) {
                        @Override
                        public void onNext(Integer t) {
                            child.onNext(t);
                        }
                        @Override
                        public void onError(Throwable e) {
                            child.onError(e);
                        }
                        @Override
                        public void onCompleted() {
                            child.onCompleted();
                        }
                    };


            Subscriber<Integer> secondary = new Subscriber<Integer>() {

                boolean done;

                @Override
                public void onNext(Integer t) {
                    if (t == 0) {
                        onCompleted();
                    }
                }
                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }
                @Override
                public void onCompleted() {                 // (1)
                    if (!done) {
                        done = true;
                        child.onCompleted();
                        main.unsubscribe();
                        unsubscribe();
                    }
                }
            };
            child.add(main);
            child.add(secondary);

            other.unsafeSubscribe(secondary);

            return main;
        };

        Observable<Integer> source = Observable.timer(30, 30, TimeUnit.MILLISECONDS)
                        .map(v -> v.intValue());

        source.lift(takeUntilZero).unsafeSubscribe(
                Subscribers.create(
                        System.out::println,
                        Throwable::printStackTrace,
                        () -> System.out.println("Done")
                )
        );

        Thread.sleep(1000);
    }


}
