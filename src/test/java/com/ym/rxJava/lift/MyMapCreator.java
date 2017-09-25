package com.ym.rxJava.lift;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by yangm on 2017/9/18.
 */

public class MyMapCreator {



    class MyMap<T,R> implements Observable.Operator<R, T> {

        private Func1<T, R> transformer;

        public MyMap() {
        }

        public MyMap(Func1<T, R> transformer) {
            this.transformer = transformer;
        }

        public <T, R> MyMap<T, R> create(Func1<T, R> transformer) {
            return new MyMap<T, R>(transformer);
        }

        @Override
        public Subscriber<? super T> call(Subscriber<? super R> subscriber) {
            return new InnerSubscriber<T>(subscriber);
        };

        class InnerSubscriber<V> extends Subscriber<V> {

            private Subscriber<? super R> child;

            public InnerSubscriber(Subscriber<? super R> child) {
                this.child = child;
            }

            @Override
            public void onCompleted() {
                if (!child.isUnsubscribed())
                    child.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                if (!child.isUnsubscribed())
                    child.onError(e);
            }

            @Override
            public void onNext(V t) {
                if (!child.isUnsubscribed()){
                    child.onNext(transformer.call(((T) t)));
                }
            }
        }
    }

}
