package com.ym.guava;

import com.google.common.util.concurrent.SettableFuture;

/**
 * Created by yangm on 2017/8/27.
 */
public class SettableFutureTest {

    public static void main(String[] args) {
        SettableFuture sf = SettableFuture.create();
        //设置成功后返回指定的信息
        sf.set("SUCCESS");
        //设置失败后返回特定的异常信息
        sf.setException(new RuntimeException("Fails"));
    }
}
