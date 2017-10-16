package com.ym.rxJava.lift.FunctionalInterface;

/**
 * Created by ym on 2017/10/15.
 */
public
interface IObserver<T> {
    void onNext(T t);
    void onError(Throwable e);
    void onCompleted();
}
