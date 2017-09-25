package com.ym.netty.executor;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public interface ScheduledFuture<V> extends Future<V>, java.util.concurrent.ScheduledFuture<V> {
}
