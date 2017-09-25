package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/8.
 */
public class SchedulerTest {

    @Test
    public void testThread() throws Exception {
        final BehaviorSubject<Integer> subject = BehaviorSubject.create();
        subject.subscribe(i -> {
            System.out.println("Received " + i + " on " + Thread.currentThread().getId());
        });

        int[] i = {1}; // naughty side-effects for examples only ;)
        Runnable r = () -> {
            synchronized(i) {
                System.out.println("onNext(" + i[0] + ") on " + Thread.currentThread().getId());
                subject.onNext(i[0]++);
            }
        };

        r.run(); // Execute on main thread
        new Thread(r).start();
        new Thread(r).start();
    }

    @Test
    public void testSubscribeOn() throws Exception {
        System.out.println("Main: " + Thread.currentThread().getId());

        Observable.create(o -> {
            System.out.println("Created on " + Thread.currentThread().getId());
            o.onNext(1);
            o.onNext(2);
            o.onCompleted();
        })
                .subscribeOn(Schedulers.newThread())
//                .observeOn(Schedulers.newThread())
                .subscribe(i -> {
                    System.out.println("Received " + i + " on " + Thread.currentThread().getId());
                });

        System.out.println("Finished main: " + Thread.currentThread().getId());
        System.in.read();
    }

    @Test
    public void testInterval() throws Exception {
        System.out.println("Main: " + Thread.currentThread().getId());

        Observable.interval(100, TimeUnit.MILLISECONDS)
                .subscribe(i -> {
                    System.out.println("Received " + i + " on " + Thread.currentThread().getId());
                });

        System.out.println("Finished main: " + Thread.currentThread().getId());
        System.in.read();
    }

    @Test
    public void testObserveOn() throws Exception {
        Observable.create(o -> {
            System.out.println("Created on " + Thread.currentThread().getId());
            o.onNext(1);
            o.onNext(2);
            o.onCompleted();
        })
                .observeOn(Schedulers.newThread())
                .subscribe(i ->
                        System.out.println("Received " + i + " on " + Thread.currentThread().getId()));

    }

    @Test
    public void testOberveOn2() throws Exception {
        Observable.create(o -> {
            System.out.println("Created on " + Thread.currentThread().getId());
            o.onNext(1);
            o.onNext(2);
            o.onCompleted();
        })
                .doOnNext(i ->
                        System.out.println("Before " + i + " on " + Thread.currentThread().getId()))
                .observeOn(Schedulers.newThread())
                .doOnNext(i ->
                        System.out.println("After " + i + " on " + Thread.currentThread().getId()))
                .subscribe(new PrintSubscriber("testObserveOn2"));
        System.in.read();
    }

    @Test
    public void testUnsubscribeOn() throws Exception {
        Observable<Object> source = Observable.using(
                () -> {
                    System.out.println("Subscribed on " + Thread.currentThread().getId());
                    return Arrays.asList(1,2);
                },
                (ints) -> {
                    System.out.println("Producing on " + Thread.currentThread().getId());
                    return Observable.from(ints);
                },
                (ints) -> {
                    System.out.println("Unubscribed on " + Thread.currentThread().getId());
                }
        );

        source
                .unsubscribeOn(Schedulers.newThread())
                .subscribe(System.out::println);
    }

    @Test
    public void testWorker() throws Exception {
        Scheduler scheduler = Schedulers.newThread();
        long start = System.currentTimeMillis();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> System.out.println(System.currentTimeMillis()-start),
                5, TimeUnit.SECONDS);
        worker.schedule(
                () -> System.out.println(System.currentTimeMillis()-start),
                5, TimeUnit.SECONDS);
        System.in.read();
    }

    @Test
    public void testWorkerUnsubscribe() throws Exception {
        Scheduler scheduler = Schedulers.newThread();
        long start = System.currentTimeMillis();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> {
                    System.out.println(System.currentTimeMillis()-start);
                    worker.unsubscribe();
                },
                5, TimeUnit.SECONDS);
        worker.schedule(
                () -> System.out.println(System.currentTimeMillis()-start),
                5, TimeUnit.SECONDS);
        System.in.read();
    }

    @Test
    public void testWorkerUnsubscribe2() throws Exception {
        Scheduler scheduler = Schedulers.newThread();
        long start = System.currentTimeMillis();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("Action completed");
            } catch (InterruptedException e) {
                System.out.println("Action interrupted");
            }
        });
        Thread.sleep(500);
        worker.unsubscribe();
        System.in.read();
    }

    @Test
    public void testImmediateScheduler() throws Exception {
        // ImmediateScheduler 并没有做任何线程调度。只是同步的执行任务。嵌套调用会导致任务被递归执行：
        Scheduler scheduler = Schedulers.immediate();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(() -> {
            System.out.println("Start");
            worker.schedule(() -> System.out.println("Inner"));
            System.out.println("End");
        });
    }

    @Test
    public void testTrampolineScheduler() throws Exception {
        // TrampolineScheduler 也是同步执行，但是不嵌套任务。而是把后来的任务添加到任务队列中，等前面的任务执行完了 再执行后面的。
        Scheduler scheduler = Schedulers.trampoline();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(() -> {
            System.out.println("Start");
            worker.schedule(() -> System.out.println("Inner"));
            System.out.println("End");
        });
    }

    @Test
    public void testNewThreadScheduler() throws Exception {
        printThread("Main");
        Scheduler scheduler = Schedulers.newThread();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(() -> {
            printThread("Start");
            worker.schedule(() -> printThread("Inner"));
            printThread("End");
        });
        Thread.sleep(500);
        worker.schedule(() -> printThread("Again"));
    }

    public static void printThread(String message) {
        System.out.println(message + " on " + Thread.currentThread().getId());
    }
}
