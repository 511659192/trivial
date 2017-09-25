package com.jd.biz.service.util.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public interface EventExecutorGroup extends ScheduledExecutorService {

    boolean isShuttingDown();

    Future<?> shutdownGracefully();

    Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);

    Future<?> terminationFuture();

    EventExecutor next();

    @Override
    Future<?> submit(Runnable task);

    @Override
    <T> Future<T> submit(Runnable task, T result);

    @Override
    <T> Future<T> submit(Callable<T> task);

    @Override
    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);

    @Override
    <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
}
