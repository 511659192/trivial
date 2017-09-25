package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.internal.operators.BufferUntilSubscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yangm on 2017/9/25.
 */
public class ValueDelayerTest {

    @Test
    public void test() throws Exception {
        class ValueDelayer<T> implements Observable.Operator<Observable<T>, T> {
            final Scheduler scheduler;
            final long delay;
            final TimeUnit unit;

            public ValueDelayer(long delay, TimeUnit unit, Scheduler scheduler) {
                this.delay = delay;
                this.unit = unit;
                this.scheduler = scheduler;
            }

            @Override
            public Subscriber<? super T> call(Subscriber<? super Observable<T>> child) {
                Scheduler.Worker w = scheduler.createWorker();
                child.add(w);

                Subscriber<T> parent = new Subscriber<T>(child, false) {
                    @Override
                    public void onNext(T t) {
                        BufferUntilSubscriber<T> bus = BufferUntilSubscriber.create();

                        w.schedule(() -> {
                            bus.onNext(t);
                            bus.onCompleted();
                        }, delay, unit);

                        System.out.println("------------------");
                        child.onNext(bus);
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

                child.add(parent);

                return parent;
            }
        }

        Observable.range(1, 3)
                .lift(new ValueDelayer<>(1, TimeUnit.SECONDS, Schedulers.computation()))
                .take(1)
                .doOnNext(v -> v.subscribe(System.out::println))
                .subscribe();

        Thread.sleep(1500);
    }

    @Test
    public void test2() throws Exception {
        class ValueDelayer<T> implements Observable.Operator<Observable<T>, T> {
            final Scheduler scheduler;
            final long delay;
            final TimeUnit unit;

            public ValueDelayer(long delay, TimeUnit unit, Scheduler scheduler) {
                this.delay = delay;
                this.unit = unit;
                this.scheduler = scheduler;
            }

            @Override
            public Subscriber<? super T> call(Subscriber<? super Observable<T>> child) {
                Scheduler.Worker w = scheduler.createWorker();

                final AtomicBoolean once = new AtomicBoolean();
                final AtomicInteger wip = new AtomicInteger(1);           // (1)

                Subscriber<T> parent = new Subscriber<T>(child, false) {
                    @Override
                    public void onNext(T t) {
                        if (wip.getAndIncrement() == 0) {                 // (2)
                            wip.set(0);
                            return;
                        }
                        BufferUntilSubscriber<T> bus = BufferUntilSubscriber.create();
                        w.schedule(() -> {
                            try {
                                bus.onNext(t);
                                bus.onCompleted();
                            } finally {
                                release();                                // (3)
                            }
                        }, delay, unit);

                        child.onNext(bus);
                        if (child.isUnsubscribed()) {
                            if (once.compareAndSet(false, true)) {        // (4)
                                release();
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }
                    @Override
                    public void onCompleted() {
                        if (once.compareAndSet(false, true)) {
                            release();                                    // (5)
                        }
                        child.onCompleted();
                    }
                    void release() {
                        if (wip.decrementAndGet() == 0) {
                            w.unsubscribe();
                        }
                    }
                };
                parent.add(Subscriptions.create(() -> {                   // (6)
                    if (once.compareAndSet(false, true)) {
                        if (wip.decrementAndGet() == 0) {
                            w.unsubscribe();
                        }
                    }
                }));

                child.add(parent);

                return parent;
            }
        }

        Observable.range(1, 3)
                .lift(new ValueDelayer<>(1, TimeUnit.SECONDS, Schedulers.computation()))
                .take(1)
                .doOnNext(v -> v.subscribe(System.out::println))
                .subscribe();

        Thread.sleep(1500);
    }
}
