package com.ym.netty.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public class DefaultPromise <V> extends AbstractFuture<V> implements Promise<V> {

    static Logger logger = LoggerFactory.getLogger(DefaultPromise.class);

    private final EventExecutor executor;
    private Object listeners;
    private volatile Object result;
    private short waiters;
    private static final int MAX_LISTENER_STACK_DEPTH = 8;
    private static final ThreadLocal<Integer> LISTENER_STACK_DEPTH = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private static final Signal SUCCESS = Signal.valueOf(DefaultPromise.class.getName() + ".SUCCESS");
    private static final Signal UNCANCELLABLE = Signal.valueOf(DefaultPromise.class.getName() + ".UNCANCELLABLE");
    private static final CauseHolder CANCELLATION_CAUSE_HOLDER = new CauseHolder(new CancellationException());

    public DefaultPromise(EventExecutor executor) {
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        this.executor = executor;
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        if (setFailure0(cause)) {
            notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this, cause);
    }

    private boolean setFailure0(Throwable cause) {
        if (isDone()) {
            return false;
        }

        synchronized (this) {
            // Allow only once.
            if (isDone()) {
                return false;
            }

            result = new CauseHolder(cause);
            if (hasWaiters()) {
                notifyAll();
            }
        }
        return true;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Object result = this.result;
        if (isDone0(result) || result == UNCANCELLABLE) {
            return false;
        }

        synchronized (this) {
            // Allow only once.
            result = this.result;
            if (isDone0(result) || result == UNCANCELLABLE) {
                return false;
            }

            this.result = CANCELLATION_CAUSE_HOLDER;
            if (hasWaiters()) {
                notifyAll();
            }
        }

        notifyListeners();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled0(result);
    }

    private static boolean isCancelled0(Object result) {
        return result instanceof CauseHolder && ((CauseHolder) result).cause instanceof CancellationException;
    }

    @Override
    public boolean isDone() {
        return isDone0(result);
    }

    private static boolean isDone0(Object result) {
        return result != null && result != UNCANCELLABLE;
    }

    private boolean hasWaiters() {
        return waiters > 0;
    }

    @Override
    public Promise<V> await() throws InterruptedException {
        if (isDone()) {
            return this;
        }

        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        synchronized (this) {
            while (!isDone()) {
                checkDeadLock();
                incWaiters();
                try {
                    wait();
                } finally {
                    decWaiters();
                }
            }
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit)
            throws InterruptedException {
        return await0(unit.toNanos(timeout), true);
    }

    private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
        if (isDone()) {
            return true;
        }

        if (timeoutNanos <= 0) {
            return isDone();
        }

        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        long startTime = System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;

        try {
            synchronized (this) {
                if (isDone()) {
                    return true;
                }

                if (waitTime <= 0) {
                    return isDone();
                }

                checkDeadLock();
                incWaiters();
                try {
                    for (;;) {
                        try {
                            wait(waitTime / 1000000, (int) (waitTime % 1000000));
                        } catch (InterruptedException e) {
                            if (interruptable) {
                                throw e;
                            } else {
                                interrupted = true;
                            }
                        }

                        if (isDone()) {
                            return true;
                        } else {
                            waitTime = timeoutNanos - (System.nanoTime() - startTime);
                            if (waitTime <= 0) {
                                return isDone();
                            }
                        }
                    }
                } finally {
                    decWaiters();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void incWaiters() {
        if (waiters == Short.MAX_VALUE) {
            throw new IllegalStateException("too many waiters: " + this);
        }
        waiters ++;
    }

    private void decWaiters() {
        waiters --;
    }

    protected void checkDeadLock() {
        EventExecutor e = executor();
        if (e != null && e.inEventLoop()) {
            throw new BlockingOperationException(toString());
        }
    }

    @Override
    public Throwable cause() {
        Object result = this.result;
        if (result instanceof CauseHolder) {
            return ((CauseHolder) result).cause;
        }
        return null;
    }

    @Override
    public V getNow() {
        Object result = this.result;
        if (result instanceof CauseHolder || result == SUCCESS) {
            return null;
        }
        return (V) result;
    }

    private static final class CauseHolder {
        final Throwable cause;
        private CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }

    protected EventExecutor executor() {
        return executor;
    }

    private void notifyListeners() {
        // This method doesn't need synchronization because:
        // 1) This method is always called after synchronized (this) block.
        //    Hence any listener list modification happens-before this method.
        // 2) This method is called only when 'done' is true.  Once 'done'
        //    becomes true, the listener list is never modified - see add/removeListener()

        Object listeners = this.listeners;
        if (listeners == null) {
            return;
        }

        this.listeners = null;

        EventExecutor executor = executor();
        if (executor.inEventLoop()) {
            final Integer stackDepth = LISTENER_STACK_DEPTH.get();
            if (stackDepth < MAX_LISTENER_STACK_DEPTH) {
                LISTENER_STACK_DEPTH.set(stackDepth + 1);
                try {
                    if (listeners instanceof DefaultFutureListeners) {
                        notifyListeners0(this, (DefaultFutureListeners) listeners);
                    } else {
                        @SuppressWarnings("unchecked")
                        final GenericFutureListener<? extends Future<V>> l =
                                (GenericFutureListener<? extends Future<V>>) listeners;
                        notifyListener0(this, l);
                    }
                } finally {
                    LISTENER_STACK_DEPTH.set(stackDepth);
                }
                return;
            }
        }

        try {
            if (listeners instanceof DefaultFutureListeners) {
                final DefaultFutureListeners dfl = (DefaultFutureListeners) listeners;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        notifyListeners0(DefaultPromise.this, dfl);
                    }
                });
            } else {
                @SuppressWarnings("unchecked")
                final GenericFutureListener<? extends Future<V>> l =
                        (GenericFutureListener<? extends Future<V>>) listeners;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        notifyListener0(DefaultPromise.this, l);
                    }
                });
            }
        } catch (Throwable t) {
            logger.error("Failed to notify listener(s). Event loop shut down?", t);
        }
    }

    private static void notifyListeners0(Future<?> future, DefaultFutureListeners listeners) {
        final GenericFutureListener<?>[] a = listeners.listeners();
        final int size = listeners.size();
        for (int i = 0; i < size; i ++) {
            notifyListener0(future, a[i]);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static void notifyListener0(Future future, GenericFutureListener l) {
        try {
            l.operationComplete(future);
        } catch (Throwable t) {
            if (logger.isWarnEnabled()) {
                logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationComplete()", t);
            }
        }
    }

    @Override
    public Promise<V> setSuccess(V result) {
        if (setSuccess0(result)) {
            notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this);
    }

    private boolean setSuccess0(V result) {
        if (isDone()) {
            return false;
        }

        synchronized (this) {
            // Allow only once.
            if (isDone()) {
                return false;
            }
            if (result == null) {
                this.result = SUCCESS;
            } else {
                this.result = result;
            }
            if (hasWaiters()) {
                notifyAll();
            }
        }
        return true;
    }

    @Override
    public boolean setUncancellable() {
        Object result = this.result;
        if (isDone0(result)) {
            return false;
        }

        synchronized (this) {
            // Allow only once.
            result = this.result;
            if (isDone0(result)) {
                return false;
            }

            this.result = UNCANCELLABLE;
        }
        return true;
    }

    @Override
    public boolean isSuccess() {
        Object result = this.result;
        if (result == null || result == UNCANCELLABLE) {
            return false;
        }
        return !(result instanceof CauseHolder);
    }

    @Override
    public boolean isCancellable() {
        return result == null;
    }

    protected static void notifyListener(
            final EventExecutor eventExecutor, final Future<?> future, final GenericFutureListener<?> l) {

        if (eventExecutor.inEventLoop()) {
            final Integer stackDepth = LISTENER_STACK_DEPTH.get();
            if (stackDepth < MAX_LISTENER_STACK_DEPTH) {
                LISTENER_STACK_DEPTH.set(stackDepth + 1);
                try {
                    notifyListener0(future, l);
                } finally {
                    LISTENER_STACK_DEPTH.set(stackDepth);
                }
                return;
            }
        }

        try {
            eventExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    notifyListener0(future, l);
                }
            });
        } catch (Throwable t) {
            logger.error("Failed to notify a listener. Event loop shut down?", t);
        }
    }

    @Override
    public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }

        if (isDone()) {
            notifyListener(executor(), this, listener);
            return this;
        }

        synchronized (this) {
            if (!isDone()) {
                if (listeners == null) {
                    listeners = listener;
                } else {
                    if (listeners instanceof DefaultFutureListeners) {
                        ((DefaultFutureListeners) listeners).add(listener);
                    } else {
                        @SuppressWarnings("unchecked")
                        final GenericFutureListener<? extends Future<V>> firstListener =
                                (GenericFutureListener<? extends Future<V>>) listeners;
                        listeners = new DefaultFutureListeners(firstListener, listener);
                    }
                }
                return this;
            }
        }

        notifyListener(executor(), this, listener);
        return this;
    }

    @Override
    public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        if (listeners == null) {
            throw new NullPointerException("listeners");
        }

        for (GenericFutureListener<? extends Future<? super V>> l: listeners) {
            if (l == null) {
                break;
            }
            addListener(l);
        }
        return this;
    }

    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }

        if (isDone()) {
            return this;
        }

        synchronized (this) {
            if (!isDone()) {
                if (listeners instanceof DefaultFutureListeners) {
                    ((DefaultFutureListeners) listeners).remove(listener);
                } else if (listeners == listener) {
                    listeners = null;
                }
            }
        }

        return this;
    }

    @Override
    public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        if (listeners == null) {
            throw new NullPointerException("listeners");
        }

        for (GenericFutureListener<? extends Future<? super V>> l: listeners) {
            if (l == null) {
                break;
            }
            removeListener(l);
        }
        return this;
    }
}
