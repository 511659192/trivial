package com.ym.netty.executor;


import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
final class ScheduledFutureTask<V> extends PromiseTask<V> implements ScheduledFuture<V> {

    private final Queue<ScheduledFutureTask<?>> delayedTaskQueue;
    private long deadlineNanos;
    private static final long START_TIME = System.nanoTime();
    private final long periodNanos;
    private static final AtomicLong nextTaskId = new AtomicLong();
    private final long id = nextTaskId.getAndIncrement();

    static long nanoTime() {
        return System.nanoTime() - START_TIME;
    }

    static long deadlineNanos(long delay) {
        return nanoTime() + delay;
    }

    ScheduledFutureTask(
            EventExecutor executor, Queue<ScheduledFutureTask<?>> delayedTaskQueue,
            Runnable runnable, V result, long nanoTime) {

        this(executor, delayedTaskQueue, toCallable(runnable, result), nanoTime);
    }

    ScheduledFutureTask(
            EventExecutor executor, Queue<ScheduledFutureTask<?>> delayedTaskQueue,
            Callable<V> callable, long nanoTime, long period) {

        super(executor, callable);
        if (period == 0) {
            throw new IllegalArgumentException("period: 0 (expected: != 0)");
        }
        this.delayedTaskQueue = delayedTaskQueue;
        deadlineNanos = nanoTime;
        periodNanos = period;
    }

    ScheduledFutureTask(
            EventExecutor executor, Queue<ScheduledFutureTask<?>> delayedTaskQueue,
            Callable<V> callable, long nanoTime) {

        super(executor, callable);
        this.delayedTaskQueue = delayedTaskQueue;
        deadlineNanos = nanoTime;
        periodNanos = 0;
    }

    public long deadlineNanos() {
        return deadlineNanos;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delayNanos(), TimeUnit.NANOSECONDS);
    }

    public long delayNanos() {
        return Math.max(0, deadlineNanos() - nanoTime());
    }

    @Override
    public int compareTo(Delayed o) {
        if (this == o) {
            return 0;
        }

        ScheduledFutureTask<?> that = (ScheduledFutureTask<?>) o;
        long d = deadlineNanos() - that.deadlineNanos();
        if (d < 0) {
            return -1;
        } else if (d > 0) {
            return 1;
        } else if (id < that.id) {
            return -1;
        } else if (id == that.id) {
            throw new Error();
        } else {
            return 1;
        }
    }

    @Override
    public void run() {
        assert executor().inEventLoop();
        try {
            if (periodNanos == 0) {
                if (setUncancellableInternal()) {
                    V result = task.call();
                    setSuccessInternal(result);
                }
            } else {
                // check if is done as it may was cancelled
                if (!isCancelled()) {
                    task.call();
                    if (!executor().isShutdown()) {
                        long p = periodNanos;
                        if (p > 0) {
                            deadlineNanos += p;
                        } else {
                            deadlineNanos = nanoTime() - p;
                        }
                        if (!isCancelled()) {
                            delayedTaskQueue.add(this);
                        }
                    }
                }
            }
        } catch (Throwable cause) {
            setFailureInternal(cause);
        }
    }
}
