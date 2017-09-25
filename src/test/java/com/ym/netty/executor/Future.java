package com.ym.netty.executor;

import java.util.concurrent.TimeUnit;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public interface Future<V> extends java.util.concurrent.Future<V> {

    Future<V> await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    Throwable cause();

    V getNow();

    boolean isSuccess();

    boolean isCancellable();

    Future<V> addListener(GenericFutureListener<? extends Future<? super V>> listener);

    Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners);
}
