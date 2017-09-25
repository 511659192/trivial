package com.ym.rxJava.lift;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by yangm on 2017/9/18.
 */

public class MyMapCreator {

    public MyMap create(Func1<Integer, String> transfer) {
        return new MyMap();
    }

    public MyMap create() {
        return new MyMap();
    }

    class MyMap implements Observable.Operator<String, Integer> {

        private Func1<Integer, String> transformer;

        public MyMap() {
        }

        public MyMap(Func1<Integer, String> transformer) {
            this.transformer = transformer;
        }

        public MyMap create(Func1<Integer, String> transformer) {
            return new MyMap(transformer);
        }

        @Override
        public Subscriber<? super Integer> call(Subscriber<? super String> subscriber) {
            return new AbstractSubscriber<Integer, String>(subscriber, Transfer.instance){}.instance();
        };
    }

    static class Transfer implements Func1<java.lang.Integer, java.lang.String> {

        private static Transfer instance = new Transfer();
        @Override
        public java.lang.String call(java.lang.Integer integer) {
            return integer + "faefafe";
        }
    }
}
