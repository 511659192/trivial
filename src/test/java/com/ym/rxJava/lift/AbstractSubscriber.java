package com.ym.rxJava.lift;

import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by yangm on 2017/9/19.
 */
public class AbstractSubscriber<P, N> {

    private Subscriber<? super N> subscriber;

    private Func1<P, N> transformer = null;

    public AbstractSubscriber(Subscriber<? super N> subscriber) {
        this.subscriber = subscriber;
    }

    public AbstractSubscriber(Subscriber<? super N> subscriber, Func1<P, N> transformer) {
        this.subscriber = subscriber;
        this.transformer = transformer;
    }

    public InnerSubscrber instance() {
        return new InnerSubscrber();
    }

    public void doOnNext(P p) {
        if (transformer == null) {
            subscriber.onNext(((N) p));
            return;
        }
        subscriber.onNext(transformer.call(p));
    }

    class InnerSubscrber extends Subscriber<P>{

        @Override
        public void onCompleted() {
            subscriber.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            subscriber.onError(e);
        }

        @Override
        public void onNext(P t) {
            doOnNext(t);
        }
    }


}
