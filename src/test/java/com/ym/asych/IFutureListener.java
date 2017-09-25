package com.ym.asych;

/**
 * Created by ym on 2017/4/23.
 */
public interface IFutureListener<V> {
    void operationCompleted(IFuture<V> vAbstractFuture) throws Exception;
}
