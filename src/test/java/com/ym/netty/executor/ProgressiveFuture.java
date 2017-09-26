package com.ym.netty.executor;

/**
 * A {@link Future} which is used to indicate the progress of an operation.
 */
public interface ProgressiveFuture<V> extends Future<V> {

    @Override
    ProgressiveFuture<V> addListener(GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    ProgressiveFuture<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    ProgressiveFuture<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    ProgressiveFuture<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    ProgressiveFuture<V> await() throws InterruptedException;

}
