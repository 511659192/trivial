package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/5.
 */
public class ErrorTest {

    @Test
    public void testOnErrorReturn() throws Exception {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Rx");
            o.onNext("is");
//            o.onError(new Exception("adjective unknown"));
            o.onNext("Rx");
            o.onNext("is");
            o.onCompleted();
        });

        values
//                .onErrorReturn(e -> "Error: " + e.getMessage())
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("faefafefe");
                    }
                })
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("000000000000000000000000000");
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                        throw new RuntimeException("afef");
                    }
                });
    }

    @Test
    public void testOnErrorResumeNext() throws Exception {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception("Oops"));
        });

        values
//                .onErrorResumeNext(Observable.just(Integer.MAX_VALUE))
                .onErrorResumeNext(o -> Observable.error(o))
                .subscribe(new PrintSubscriber("with onError: "));
    }

    @Test
    public void testonExceptionResumeNext() throws Exception {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Rx");
            o.onNext("is");
            //o.onError(new Throwable() {}); // 这个为 error 不会捕获
            o.onError(new Exception()); // 这个为 Exception 会被捕获
        });

        values
//                .onExceptionResumeNext(Observable.just("hard"))
                // 利用这个操作函数可以实现把一个异常信息包装起来再次抛出
                .onErrorResumeNext(e -> Observable.error(new UnsupportedOperationException(e)))
                .subscribe(v -> System.out.println(v));
    }

    @Test
    public void testRetry() throws Exception {
        Random random = new Random();
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(random.nextInt() % 20);
            o.onNext(random.nextInt() % 20);
            o.onError(new Exception());
        });

        values
                .retry(1)
                .subscribe(v -> System.out.println(v));
    }

    @Test
    public void testRetryWhen() throws Exception {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception("Failed"));
        });

        source.retryWhen((o) -> o
                .take(2)
                .delay(100, TimeUnit.MILLISECONDS)
        )
                .timeInterval()
                .subscribe(
                        System.out::println,
                        System.out::println);
        System.in.read();
    }

    @Test
    public void testUsing() throws Exception {
        Observable<Character> values = Observable.using(
                () -> {
                    String resource = "MyResource";
                    System.out.println("Leased: " + resource);
                    return resource;
                },
                (resource) -> {
                    return Observable.create(o -> {
                        for (Character c : resource.toCharArray()){
                            o.onNext(c);
                        }
                        o.onCompleted();
                    });
                },
                (resource) -> System.out.println("Disposed: " + resource));

        values
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println(e));


    }
}
