package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.observers.TestSubscriber;

/**
 * Created by yangm on 2017/9/25.
 */
public class OnSubscribeRunActionTest {

    static class OnSubscribeRunAction<T>
            implements Observable.OnSubscribe<T> {
        final Observable actual;
        final Action0 action;
        public OnSubscribeRunAction(Observable actual, Action0 action) {
            this.action = action;
            this.actual = actual;
        }
        @Override
        public void call(Subscriber child) {
            try {
                action.call();
            } catch (Throwable e) {
                child.onError(e);
                return;
            }
            actual.unsafeSubscribe(child);
        }
    }

    @Test
    public void test1() throws Exception {
        Observable<Integer> source = Observable.create(
                new OnSubscribeRunAction<>(Observable.range(1, 3),
                        () -> {
                            System.out.println("Subscribing!");
                        }));

        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onStart() {
//                Thread t = new Thread(() -> {
//                    System.out.println("Starting helper thread "
//                            + Thread.currentThread());
//                });
//                t.start();
                System.out.println("--------------------");
            }
        };
        source.unsafeSubscribe(ts);
    }

    static class OnSubscribeRunAction2<T>
            implements Observable.OnSubscribe<T> {
        final Observable actual;
        final Action0 action;
        public OnSubscribeRunAction2(Observable actual, Action0 action) {
            this.action = action;
            this.actual = actual;
        }
        @Override
        public void call(Subscriber child) {
            try {
                action.call();
            } catch (Throwable e) {
                child.onError(e);
                return;
            }
            actual.unsafeSubscribe(new Subscriber() {
                @Override
                public void onCompleted() {
                    child.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }

                @Override
                public void onNext(Object o) {
                    child.onNext(o);
                }
            });
        }
    }

    @Test
    public void test2() throws Exception {
        Observable<Integer> source = Observable.create(
                new OnSubscribeRunAction2<>(Observable.range(1, 3),
                        () -> {
                            System.out.println("Subscribing!");
                        }));

        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onStart() {
//                Thread t = new Thread(() -> {
//                    System.out.println("Starting helper thread "
//                            + Thread.currentThread());
//                });
//                t.start();
                System.out.println("--------------------");
            }
        };
        source.unsafeSubscribe(ts);
    }
}
