package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.observers.TestSubscriber;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cdyangmeng on 2017/9/26.
 */
public class ExecutorSchedulerTest {

    public static final ScheduledExecutorService genericScheduler;
    static {
        genericScheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "GenericScheduler");
            t.setDaemon(true);
            return t;
        });
    }

    static class ExecutorScheduler extends Scheduler {

        final AtomicInteger wip = new AtomicInteger();
        final Queue<ScheduledAction> queue = new ConcurrentLinkedQueue<>();
        final CompositeSubscription tracking = new CompositeSubscription();

        final Executor exec;
        public ExecutorScheduler(Executor exec) {
            this.exec = exec;
        }
        @Override
        public Worker createWorker() {
            return new ExecutorWorker();
        }

        final class ExecutorWorker extends Worker implements Runnable {                             // (1)
            // data fields here
            @Override
            public Subscription schedule(Action0 action) {
                if (isUnsubscribed()) {
                    return Subscriptions.unsubscribed();
                }
                ScheduledAction sa = new ScheduledAction(action);
                tracking.add(sa);
                sa.add(Subscriptions.create(() -> tracking.remove(sa)));        // (1)

                queue.offer(sa);                            // (2)

                sa.add(Subscriptions.create(() -> queue.remove(sa)));           // (3)

                if (wip.getAndIncrement() == 0) {           // (4)
                    exec.execute(this);                     // (5)
                }

                return sa;
            }
            @Override
            public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {

                if (delayTime <= 0) {
                    return schedule(action);                      // (1)
                }
                if (isUnsubscribed()) {
                    return Subscriptions.unsubscribed();          // (2)
                }

                ScheduledAction sa = new ScheduledAction(action);
                tracking.add(sa);
                sa.add(Subscriptions.create(() -> tracking.remove(sa)));              // (3)

                ScheduledExecutorService schedex;
                if (exec instanceof ScheduledExecutorService) {
                    schedex = (ScheduledExecutorService) exec;    // (4)
                } else {
                    schedex = genericScheduler;      // (5)
                }

                Future<?> f = schedex.schedule(() -> {            // (6)
                    queue.offer(sa);                              // (7)
                    sa.add(Subscriptions.create(() -> queue.remove(sa)));
                    if (wip.getAndIncrement() == 0) {
                        exec.execute(this);
                    }
                }, delayTime, unit);

                sa.add(Subscriptions.create(() -> f.cancel(false)));                  // (8)
                return sa;
            }
            @Override
            public void run() {
                do {
                    if (isUnsubscribed()) {                   // (1)
                        queue.clear();
                        return;
                    }
                    ScheduledAction sa = queue.poll();        // (2)
                    if (sa != null && !sa.isUnsubscribed()) {
                        sa.run();                             // (3)
                    }
                } while (wip.decrementAndGet() > 0);          // (4)
            }

            @Override
            public boolean isUnsubscribed() {
                return tracking.isUnsubscribed();
            }

            @Override
            public void unsubscribe() {
                queue.clear();
                tracking.unsubscribe();
            }
        }
    }

    @Test
    public void test() {
        ExecutorService exec = Executors.newFixedThreadPool(3);
        try {
            Scheduler s = new ExecutorScheduler(exec);

            Observable<Integer> source = Observable.just(1)
                    .delay(500, TimeUnit.MILLISECONDS, s)
                    .doOnNext(v -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        System.out.println(Thread.currentThread());
                    });

            TestSubscriber<Integer> ts1 = new TestSubscriber<>();
            TestSubscriber<Integer> ts2 = new TestSubscriber<>();
            TestSubscriber<Integer> ts3 = new TestSubscriber<>();

            source.subscribe(ts1);
            source.subscribe(ts2);
            source.subscribe(ts3);

            ts1.awaitTerminalEvent();
            ts1.assertNoErrors();
            ts1.assertValue(1);

            ts2.awaitTerminalEvent();
            ts2.assertNoErrors();
            ts2.assertValue(1);

            ts3.awaitTerminalEvent();
            ts3.assertNoErrors();
            ts3.assertValue(1);
        } finally {
            exec.shutdown();
        }
    }
}
