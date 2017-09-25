package com.ym.netty.executor;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public interface Promise<V> extends Future<V> {

    Promise<V> setSuccess(V result);

    Promise<V> setFailure(Throwable cause);

    boolean setUncancellable();

}
