package com.ym.rxJava.lift;

import com.ym.rxJava.OperatorTest;
import org.jibx.binding.Run;
import org.junit.Test;
import org.springframework.scheduling.annotation.Schedules;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.operators.OperatorMerge;
import rx.schedulers.Schedulers;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.SocketHandler;

/**
 * Created by yangm on 2017/9/18.
 */
public class Main {


    @Test
    public void testLift() throws Exception {
            Observable.just(1)
                    .doAfterTerminate(() -> {
                        System.out.println("doAfterTerminate");
                    })
                    .subscribe(System.out::println);
    }

    @Test
    public void testdoAfterTerminate() throws Exception {
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                subscriber.onNext(2);
//                subscriber.onError(new RuntimeException("runtimeException"));
                subscriber.onNext(3);
                subscriber.onNext(4);
                subscriber.onCompleted();
            }
        })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        System.out.println("integer " + integer);
                        return integer.toString();
                    }
                })
                .map(new Func1<String, Integer>() {

                    @Override
                    public Integer call(String s) {
                        System.out.println("string " + s);
                        throw new RuntimeException("afafeafef");
//                        return Integer.valueOf(s);
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e);
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext " + integer);
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        Observable.just(1, 2,3)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("doing " + integer);
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        System.out.println("map1 " + Thread.currentThread().getId() + " " + integer);
                        try {
                            sleep();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return integer + " ----";
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String integer) {
                        System.out.println("map2 " + Thread.currentThread().getId() + " " + integer);
                        try {
                            sleep();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return integer + " ****";
                    }
                })
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("subscribe " + Thread.currentThread().getId() + " " + s);
                        try {
                            sleep();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(s);
                    }
                });

        System.in.read();
    }

    public static void sleep() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(10000));
    }
}
