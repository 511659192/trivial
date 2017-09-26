package com.ym.rxJava.lift;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.*;

/**
 * Created by cdyangmeng on 2017/9/26.
 */
public class CustomWorkerTest {

    public static final ScheduledExecutorService genericScheduler;
    static {
        genericScheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "GenericScheduler");
            t.setDaemon(true);
            return t;
        });
    }

    static class CustomWorker extends Scheduler.Worker {
        final ExecutorService exec;                             // (1)
        final CompositeSubscription tracking;                   // (2)
        final boolean shutdown;                                 // (3)

        public CustomWorker() {
            exec = Executors.newSingleThreadExecutor();
            tracking = new CompositeSubscription();
            shutdown = true;
        }
        public CustomWorker(ExecutorService exec) {
            this.exec = exec;
            tracking = new CompositeSubscription();
            shutdown = false;                                   // (4)
        }
        @Override
        public Subscription schedule(Action0 action) {
            return schedule(action, 0, null);                   // (5)
        }
        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (isUnsubscribed()) {                                // (1)
                return Subscriptions.unsubscribed();
            }
            ScheduledAction sa = new ScheduledAction(action);      // (2)

            tracking.add(sa);                                      // (3)
            sa.add(Subscriptions.create(
                    () -> tracking.remove(sa)));

            Future<?> f;
            if (delayTime <= 0) {                                  // (4)
                f = exec.submit(sa);
            } else if (exec instanceof ScheduledExecutorService) { // (5)
                f = ((ScheduledExecutorService)exec).schedule(sa, delayTime, unit);
            } else {
                f = genericScheduler.schedule(() -> {              // (6)
                    Future<?> g = exec.submit(sa);
                    sa.add(Subscriptions.create(                   // (7)
                            () -> g.cancel(false)));
                }, delayTime, unit);
            }

            sa.add(Subscriptions.create(                           // (8)
                    () -> f.cancel(false)));

            return sa;                                             // (9)
        }

        @Override
        public boolean isUnsubscribed() {
            return tracking.isUnsubscribed();                   // (6)
        }
        @Override
        public void unsubscribe() {
            if (shutdown) {
                exec.shutdownNow();                             // (7)
            }
            tracking.unsubscribe();
        }
    }
}
