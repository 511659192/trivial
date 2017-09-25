package com.ym.netty.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public final class ThreadPerTaskExecutor implements Executor {
    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}
