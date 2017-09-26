package com.ym.guava;

import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by yangm on 2017/8/27.
 */
public class ListenableFutureTest {

    public static void main(String[] args) {
        int NUM_THREADS = 10;//10个线程
        ListeningExecutorService executorService =
                MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(NUM_THREADS));
        ListenableFuture<String> listenableFuture =
                executorService.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        System.out.println("call");
                        return "faeff";
                    }
                });
        listenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                //在Future任务完成之后运行的一些方法
                System.out.println("methodToRunOnFutureTaskCompletion");
            }
        }, executorService);
    }
}
