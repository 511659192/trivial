package com.ym;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static javax.swing.text.html.HTML.Tag.S;

/**
 * @Author yangmeng44
 * @Date 2017/4/21
 */
public class asynch {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // 通过submit方法提交任务
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("call " + Thread.currentThread().getId());
                return "hello world";
            }
        });

        System.out.println("等待返回的结果");
        try {
            System.out.println("out1 " + Thread.currentThread().getId());
            System.out.println("返回的结果," + future.get());
            System.out.println("out2 " + Thread.currentThread().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
