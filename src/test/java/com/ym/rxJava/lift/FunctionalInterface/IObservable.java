package com.ym.rxJava.lift.FunctionalInterface;

import com.ym.rxJava.lift.SubscribeOn;

/**
 * Created by ym on 2017/10/15.
 */
@FunctionalInterface
public interface IObservable<T> {

    IDisposable subscribe(IObserver<T> observer);
}
