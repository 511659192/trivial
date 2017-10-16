package com.ym.rxJava.lift;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.operators.BufferUntilSubscriber;
import rx.observers.SerializedSubscriber;
import rx.observers.Subscribers;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    public void testOnSubscribeRunAction() throws Exception {
        class OnSubscribeRunAction<T> implements Observable.OnSubscribe<T> {
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
//                actual.unsafeSubscribe(child);

                actual.unsafeSubscribe(new Subscriber<T>(child) {
                    @Override
                    public void onNext(T t) {
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
                });
            }
        }

        Observable<Integer> source = Observable.create(
                new OnSubscribeRunAction<>(Observable.range(1, 3),
                        () -> {
                            System.out.println("Subscribing!");
                        }));

        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onStart() {
                Thread t = new Thread(() -> {
                    System.out.println("Starting helper thread "
                            + Thread.currentThread());
                });
                t.start();
            }
        };
        source.unsafeSubscribe(ts);
    }

    @Test
    public void testOnSubscribeRunActionDelayed() throws Exception {
        class OnSubscribeRunActionDelayed<T> implements Observable.OnSubscribe<T> {
            final Observable actual;
            final Action0 action;
            final long delay;
            final TimeUnit unit;
            final Scheduler scheduler;
            public OnSubscribeRunActionDelayed(Observable actual, Action0 action, long delay, TimeUnit unit, Scheduler scheduler) {
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
                // 把 worker 和调度结果作为资源添加到 child 中，使得 child 被取消订阅时，能取消订阅所有的资源
                // 由于我们只会在这个 worker 上调度一个任务，所以取消订阅 worker 就会取消“所有”正在执行或者等待执行的任务，所以我们就没必要把调度结果也添加到 child 中，只需要添加 worker 就足够了
//                child.add(w);

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

        Observable<Integer> source = Observable.create(
                new OnSubscribeRunActionDelayed<>(Observable.just(1).delay(1, TimeUnit.SECONDS),
                        () -> {
                            System.out.println("Sorry, it takes too long...");
                        }, 500, TimeUnit.MILLISECONDS, Schedulers.io()));

        Subscription s = source.subscribe(System.out::println);
        System.out.println("-----------------");
        Thread.sleep(250);

        s.unsubscribe();

        Thread.sleep(1000);

        source.subscribe(System.out::println);
        System.out.println("================");

        Thread.sleep(1500);

        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().startsWith("RxComputationScheduler")) {
                System.out.println(t);
                System.out.println("************************");
            }
        }
    }
    
    @Test
    public void testValueDelayer() throws Exception {
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
                        System.out.println("onNext " + t);
                        BufferUntilSubscriber<T> bus = BufferUntilSubscriber.create();

                        w.schedule(() -> {
                            System.out.println("schedule " + t);
                            bus.onNext(t);
                            bus.onCompleted();
                        }, delay, unit);

                        child.onNext(bus);
                    }
                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted");
                        child.onCompleted();
                    }
                };

                child.add(parent);

                return parent;
            }
        }

        Observable.range(1, 3)
                .lift(new ValueDelayer<>(1, TimeUnit.SECONDS, Schedulers.computation()))
                // 我们希望它会在 1s 之后打印 1。问题就出在 take(1) 会在接收到第一个数据之后取消上游 Observable，而这会取消我们延迟执行的任务
                .take(1)
                .doOnNext(v -> v.subscribe(System.out::println))
                .subscribe();

        Thread.sleep(1500);
    }

    @Test
    public void testValueDelayer2() throws Exception {
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
                        System.out.println("onNext " + t);
                        if (wip.getAndIncrement() == 0) {                 // (2)
                            wip.set(0);
                            return;
                        }

                        BufferUntilSubscriber<T> bus = BufferUntilSubscriber.create();

                        w.schedule(() -> {
                            try {
                                System.out.println("schedule " + t);
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
                        System.out.println("release ");
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