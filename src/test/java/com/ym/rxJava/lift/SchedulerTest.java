package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.NewThreadWorker;
import rx.internal.schedulers.ScheduledAction;
import rx.internal.util.RxThreadFactory;
import rx.observers.TestSubscriber;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import javax.swing.*;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ym on 2017/10/14.
 */
public class SchedulerTest {

    static class ContextManager {
        static final ThreadLocal<Object> ctx = new ThreadLocal<>();

        private ContextManager() {
            throw new IllegalStateException();
        }

        public static Object get() {
            return ctx.get();
        }
        public static void set(Object context) {
            ctx.set(context);
        }
    }

    static class ContextAwareScheduler extends Scheduler {

        public static final ContextAwareScheduler INSTANCE = new ContextAwareScheduler();                       // (1)

        final NewThreadWorker worker;

        public ContextAwareScheduler() {
            this.worker = new NewThreadWorker(new RxThreadFactory("ContextAwareScheduler")); // (2)
        }

        @Override
        public Worker createWorker() {
            return new ContextAwareScheduler.ContextAwareWorker(worker);                 // (3)
        }

        static final class ContextAwareWorker extends Worker {

            final CompositeSubscription tracking;                  // (4)
            final NewThreadWorker worker;

            public ContextAwareWorker(NewThreadWorker worker) {
                this.worker = worker;
                this.tracking = new CompositeSubscription();
            }

            @Override
            public Subscription schedule(Action0 action) {
                return schedule(action, 0, null);               // (1)
            }

            @Override
            public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
                if (isUnsubscribed()) {                         // (2)
                    return Subscriptions.unsubscribed();
                }

                Object context = ContextManager.get();          // (3)
                Action0 a = () -> {
                    ContextManager.set(context);                // (4)
                    action.call();
                };

                return worker.scheduleActual(a, delayTime, unit, tracking);                 // (5)
            }

            @Override
            public boolean isUnsubscribed() {
                return tracking.isUnsubscribed();                  // (5)
            }

            @Override
            public void unsubscribe() {
                tracking.unsubscribe();
            }
        }
    }

    @Test
    public void testContextAwareWorker() throws InterruptedException {

        ContextAwareScheduler INSTANCE = ContextAwareScheduler.INSTANCE;

        Scheduler.Worker w = INSTANCE.createWorker();

        CountDownLatch cdl = new CountDownLatch(1);

        ContextManager.set(1);
        w.schedule(() -> {
            System.out.println(Thread.currentThread());
            System.out.println(ContextManager.get());
        });

        ContextManager.set(2);
        w.schedule(() -> {
            System.out.println(Thread.currentThread());
            System.out.println(ContextManager.get());
            cdl.countDown();
        });

        cdl.await();

        ContextManager.set(3);

        Observable.timer(500, TimeUnit.MILLISECONDS, INSTANCE)
                .doOnNext(v -> {
                    System.out.println(Thread.currentThread());
                    System.out.println(ContextManager.get());
                }).toBlocking().first();

        w.unsubscribe();
    }

    public static final ScheduledExecutorService genericScheduler;
    static {
        genericScheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "GenericScheduler");
//            t.setDaemon(true);
            return t;
        });
    }

    //已有的多线程 Executor 包装为一个 scheduler，并且遵循 Scheduler 和 Worker 的规则。
    //Worker 最重要的一个规则就是有序提交的非延迟任务要按序执行，但是 Executor 的线程是随机取走任务，而且是并发乱序执行的。
    //解决办法就是使用我们以前介绍过的“队列漏”，并且对调度的任务进行一个中继操作。队列会保证任务提交的顺序得到了保存，而漏的逻辑则保证了任意时刻最多只会有一个任务执行，不会出现并发执行。
    static class ExecutorScheduler extends Scheduler {

        final AtomicInteger wip = new AtomicInteger();
        final Queue<ScheduledAction> queue = new ConcurrentLinkedQueue<>();
        final CompositeSubscription tracking = new CompositeSubscription();

        // 所有的 worker 实例都会把任务转发到同一个底层的 Executor 上
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
                    int sign = wip.getAndIncrement();
                    System.out.println("on" + Thread.currentThread().getId() + " sign " + sign);
                    if (sign == 0) {
                        exec.execute(this);
                    }
                }, delayTime, unit);

                sa.add(Subscriptions.create(() -> f.cancel(false)));                  // (8)
                return sa;
            }
            @Override
            public void run() {
                do {
                    System.out.println("on" + Thread.currentThread().getId() + " run wip " + wip.get());
                    if (isUnsubscribed()) {                   // (1)
                        queue.clear();
                        return;
                    }
                    ScheduledAction sa = queue.poll();        // (2)
                    System.out.println("on" + Thread.currentThread().getId() + " run sa " + (sa == null));
                    if (sa != null && !sa.isUnsubscribed()) {
                        System.out.println("on" + Thread.currentThread().getId() + " ****************");
                        sa.run();                             // (3)
                    }
                } while (wip.decrementAndGet() > 0);          // (4)
                System.out.println("on" + Thread.currentThread().getId() + " run end");
            }

            @Override
            public boolean isUnsubscribed() {
                return tracking.isUnsubscribed();
            }

            @Override
            public void unsubscribe() {
                System.out.println("on" + Thread.currentThread().getId() + " unsubscribe " + queue.size());
//                queue.clear();
                if (queue.size() != 0) return;
                tracking.unsubscribe();
            }
        }
    }

    @Test
    public void testExecutorScheduler() throws IOException {
        ExecutorService exec = Executors.newScheduledThreadPool(3);
        try {
            Scheduler scheduler = new ExecutorScheduler(exec);

            Observable<Integer> source = Observable.just(1)
//                    .delay(500, TimeUnit.MILLISECONDS, scheduler)
                    .delay(1, TimeUnit.SECONDS, scheduler)
                    .doOnNext(v -> {
                        System.out.println("on" + Thread.currentThread().getId() + " doOnNext " + v);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });

            TestSubscriber<Integer> ts1 = new TestSubscriber<>();
            TestSubscriber<Integer> ts2 = new TestSubscriber<>();
            TestSubscriber<Integer> ts3 = new TestSubscriber<>();

            source.subscribe(ts1);
            source.subscribe(ts2);
            source.subscribe(ts3);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            exec.shutdown();
        }
    }

    @Test
    public void testDelay() throws Exception {
        Observable<Integer> source = Observable.just(1)
                .delay(500, TimeUnit.MILLISECONDS)
                .doOnNext(v -> {
                    System.out.println("doOnNext " + v);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("on" + Thread.currentThread().getId() + " testing");
                });

        TestSubscriber<Integer> ts1 = new TestSubscriber<>();
        TestSubscriber<Integer> ts2 = new TestSubscriber<>();
        TestSubscriber<Integer> ts3 = new TestSubscriber<>();

        source.subscribe(ts1);
//        source.subscribe(ts2);
//        source.subscribe(ts3);
        Thread.sleep(5000);
    }

    interface GuiEventLoop {
        void run(Runnable task);
        void cancel(Runnable task);
    }

    static class EDTEventLoop implements GuiEventLoop {
        @Override
        public void run(Runnable task) {
            SwingUtilities.invokeLater(task);
        }

        @Override
        public void cancel(Runnable task) {
            // not supported
        }
    }

    static class GuiScheduler extends Scheduler {

        final GuiEventLoop eventLoop;

        public GuiScheduler(GuiEventLoop el) {
            this.eventLoop = el;
        }

        @Override
        public Worker createWorker() {
            return new GuiWorker();
        }

        final class GuiWorker extends Worker {
            final CompositeSubscription tracking = new CompositeSubscription();
            @Override
            public void unsubscribe() {
                tracking.unsubscribe();
            }

            @Override
            public boolean isUnsubscribed() {
                return tracking.isUnsubscribed();
            }

            @Override
            public Subscription schedule(Action0 action) {
                if (isUnsubscribed()) {                             // (1)
                    return Subscriptions.unsubscribed();
                }
                ScheduledAction sa = new ScheduledAction(action);
                tracking.add(sa);
                sa.add(Subscriptions.create(() -> tracking.remove(sa)));                // (2)

                Runnable r = () -> {                                // (3)
                    if (!sa.isUnsubscribed()) {
                        sa.run();
                    }
                };

                eventLoop.run(r);                                   // (4)
                sa.add(Subscriptions.create(() -> eventLoop.cancel(r)));                // (5)
                return sa;
            }

            @Override
            public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
                if (delayTime <= 0) {                             // (1)
                    return schedule(action);
                }
                if (isUnsubscribed()) {                           // (2)
                    return Subscriptions.unsubscribed();
                }
                ScheduledAction sa = new ScheduledAction(action);
                tracking.add(sa);
                sa.add(Subscriptions.create(() -> tracking.remove(sa)));              // (3)

                Future<?> f = genericScheduler.schedule(() -> {
                    Runnable r = () -> {
                        if (!sa.isUnsubscribed()) {
                            sa.run();
                        }
                    };
                    eventLoop.run(r);
                    sa.add(Subscriptions.create(() -> eventLoop.cancel(r)));          // (5)
                }, delayTime, unit);

                sa.add(Subscriptions.create(() -> f.cancel(false)));

                return sa;
            }
        }
    }

    @Test
    public void testGuiScheduler() throws Exception {
        Scheduler s = new GuiScheduler(new EDTEventLoop());

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
    }
}