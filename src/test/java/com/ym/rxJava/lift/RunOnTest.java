package com.ym.rxJava.lift;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by cdyangmeng on 2017/9/28.
 */
public class RunOnTest {

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

    @Test
    public void test() {
        IObservable<Object> sleeper = o -> {
            try {
                Thread.sleep(1000);
                o.onCompleted();
            } catch (InterruptedException ex) {
                o.onError(ex);
            }
            return () -> {
                System.out.println("disposable");
            };
        };

        ExecutorService exec = Executors.newSingleThreadExecutor();

        IObservable<Object> subscribeOn = o -> {
            Future<?> f = exec.submit(() -> sleeper.subscribe(o));
            return () -> f.cancel(true);
        };

        ExecutorService exec2 = Executors.newSingleThreadExecutor();

        IObservable<Object> subscribeOn2 = o -> {
            Future<?> f2 = exec2.submit(() -> subscribeOn.subscribe(o));
            return () -> f2.cancel(true);
        };
    }
}
