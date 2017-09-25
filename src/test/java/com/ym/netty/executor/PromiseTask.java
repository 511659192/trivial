package com.jd.biz.service.util.executor;


import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V> {

    static <T> Callable<T> toCallable(Runnable runnable, T result) {
        return new PromiseTask.RunnableAdapter<T>(runnable, result);
    }

    private static final class RunnableAdapter<T> implements Callable<T> {
        final Runnable task;
        final T result;

        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }

        @Override
        public T call() {
            task.run();
            return result;
        }

        @Override
        public String toString() {
            return "Callable(task: " + task + ", result: " + result + ')';
        }
    }

    protected final Callable<V> task;

    PromiseTask(EventExecutor executor, Runnable runnable, V result) {
        this(executor, toCallable(runnable, result));
    }

    PromiseTask(EventExecutor executor, Callable<V> callable) {
        super(executor);
        task = callable;
    }

    @Override
    public void run() {
        try {
            if (setUncancellableInternal()) {
                V result = task.call();
                setSuccessInternal(result);
            }
        } catch (Throwable e) {
            setFailureInternal(e);
        }
    }

    protected final Promise<V> setFailureInternal(Throwable cause) {
        super.setFailure(cause);
        return this;
    }

    protected final Promise<V> setSuccessInternal(V result) {
        super.setSuccess(result);
        return this;
    }

    protected final boolean setUncancellableInternal() {
        return super.setUncancellable();
    }

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }
}
