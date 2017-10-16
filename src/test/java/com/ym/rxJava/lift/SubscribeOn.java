package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by ym on 2017/10/15.
 */
public class SubscribeOn {

    @FunctionalInterface
    interface IObservable<T> {
        IDisposable subscribe(IObserver<T> observer);
    }

    @FunctionalInterface
    interface IDisposable {
        void dispose();
    }

    interface IObserver<T> {
        void onNext(T t);
        void onError(Throwable e);
        void onCompleted();
    }

    class observer<T> implements IObserver<T> {
        @Override
        public void onNext(T t) {
            System.out.println("onNext " + t);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onCompleted() {
            System.out.println("onCompleted");
        }
    }

    @Test
    public void testSleep() throws Exception {
        IObservable<Object> sleeper = o -> {
            try {
                Thread.sleep(1000);
                o.onCompleted();
            } catch (InterruptedException ex) {
                o.onError(ex);
            }
            return () -> {};
        };

        sleeper.subscribe(new observer<Object>());
        System.out.println("----------------------------");

        ExecutorService exec = Executors.newSingleThreadExecutor();

        IObservable<Object> subscribeOn = o -> {
            Future<?> f = exec.submit(() -> sleeper.subscribe(o));
            return () -> {
                System.out.println(f.cancel(true));
            };
        };
        IDisposable ret;
        ret = subscribeOn.subscribe(new observer<>());
//        ret.dispose();
        ExecutorService exec2 = Executors.newSingleThreadExecutor();

        IObservable<Object> subscribeOn2 = o -> {
            Future<?> f2 = exec2.submit(() -> subscribeOn.subscribe(o));
            return () -> f2.cancel(true);
        };

        subscribeOn2.subscribe(new observer<>());

        TimeUnit.SECONDS.sleep(2);

        IObservable<Object> subscribeOn3 = o -> {
            Future<?> f2 = exec2.submit(() -> {
                Future<?> f = exec.submit(() -> {
                    sleeper.subscribe(o);
                });
            });
            return null;
        };
    }

    @Test
    public void testSubscribeOn() throws Exception {
        rx.Observable.just(1, 2)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);
                    }
                });
        System.in.read();
    }
}
