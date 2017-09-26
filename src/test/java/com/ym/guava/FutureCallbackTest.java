package com.ym.guava;

import com.google.common.util.concurrent.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
/**
 * Created by yangm on 2017/8/27.
 */
public class FutureCallbackTest {

    @Test
    public void testFutureCallback() {
        // 创建一个线程缓冲池Service
        ExecutorService executor = Executors.newCachedThreadPool();
        //创建一个ListeningExecutorService实例
        ListeningExecutorService executorService =
                MoreExecutors.listeningDecorator(executor);
        //提交一个可监听的线程
        ListenableFuture<String> futureTask = executorService.submit
                (new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "Task completed";
                    }
                });
        FutureCallbackImpl callback = new FutureCallbackImpl();
        //线程结果处理回调函数
        Futures.addCallback(futureTask, callback);
        //如果callback中执行的是比较费时的操作，Guava建议使用以下方法。
//        Futures.addCallback(futureTask,callback,executorService);
        //处理后的线程执行结果："Task completed successfully"
        assertThat(callback.getCallbackResult(), is("Task completed successfully"));
    }

    static class FutureCallbackImpl implements FutureCallback<String> {
        private StringBuilder builder = new StringBuilder();

        @Override
        public void onSuccess(String result) {
            builder.append(result).append(" successfully");
        }

        @Override
        public void onFailure(Throwable t) {
            builder.append(t.toString());
        }

        public String getCallbackResult() {
            return builder.toString();
        }
    }

    class FutureFallbackImpl implements FutureFallback<String> {
        @Override
        public ListenableFuture<String> create(Throwable t) throws
                Exception {
            if (t instanceof FileNotFoundException) {
                SettableFuture<String> settableFuture =
                        SettableFuture.create();
                settableFuture.set("Not Found");
                return settableFuture;
            }
            throw new Exception(t);
        }
    }
}
