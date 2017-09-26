package com.ym.netty.executor;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */

import java.util.concurrent.TimeUnit;

/**
 * A skeletal {@link Future} implementation which represents a {@link Future} which has been completed already.
 */
public abstract class CompleteFuture<V> extends AbstractFuture<V> {

    private final EventExecutor executor;

    /**
     * Creates a new instance.
     *
     * @param executor the {@link EventExecutor} associated with this future
     */
    protected CompleteFuture(EventExecutor executor) {
        this.executor = executor;
    }

    /**
     * Return the {@link EventExecutor} which is used by this {@link CompleteFuture}.
     */
    protected EventExecutor executor() {
        return executor;
    }

    @Override
    public Future<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        DefaultPromise.notifyListener(executor(), this, listener);
        return this;
    }

    @Override
    public Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        if (listeners == null) {
            throw new NullPointerException("listeners");
        }
        for (GenericFutureListener<? extends Future<? super V>> l: listeners) {
            if (l == null) {
                break;
            }
            DefaultPromise.notifyListener(executor(), this, l);
        }
        return this;
    }

    @Override
    public Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        // NOOP
        return this;
    }

    @Override
    public Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        // NOOP
        return this;
    }

    @Override
    public Future<V> await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return true;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
}

