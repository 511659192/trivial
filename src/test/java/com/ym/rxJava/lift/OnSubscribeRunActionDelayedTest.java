package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.observers.SerializedSubscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/25.
 */
public class OnSubscribeRunActionDelayedTest {

    static class OnSubscribeRunActionDelayed<T>
            implements Observable.OnSubscribe<T> {
        final Observable actual;
        final Action0 action;
        final long delay;
        final TimeUnit unit;
        final Scheduler scheduler;
        public OnSubscribeRunActionDelayed(Observable actual,
                                           Action0 action, long delay,
                                           TimeUnit unit, Scheduler scheduler) {
            this.action = action;
            this.actual = actual;
            this.delay = delay;
            this.unit = unit;
            this.scheduler = scheduler;
        }
        @Override
        public void call(Subscriber<? super T> child) {
            SerializedSubscriber<T> s = new SerializedSubscriber<>(child);

            Scheduler.Worker w = scheduler.createWorker();                 // (1)
            child.add(w);

            Subscription cancel = w.schedule(() -> {
                try {
                    action.call();
                } catch (Throwable e) {
                    s.onError(e);
                }
            }, delay, unit);

            actual
                    .doOnCompleted(cancel::unsubscribe)
                    .unsafeSubscribe(s);
        }
    }

    @Test
    public void test() throws Exception {
        Observable<Integer> source = Observable.create(
                new OnSubscribeRunActionDelayed<>(Observable
                        .just(1).delay(1, TimeUnit.SECONDS),
                        () -> {
                            System.out.println("Sorry, it takes too long...");
                        }, 500, TimeUnit.MILLISECONDS, Schedulers.io()));

        Subscription s = source.subscribe(System.out::println);
        Thread.sleep(250);
        s.unsubscribe();
        Thread.sleep(1000);
        source.subscribe(System.out::println);
        Thread.sleep(1500);
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().startsWith("RxCached")) {
                System.out.println(t);
            }
        }
        System.in.read();
    }
}
