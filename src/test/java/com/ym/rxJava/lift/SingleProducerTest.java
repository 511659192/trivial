package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cdyangmeng on 2017/10/2.
 */
public class SingleProducerTest {

    @Test
    public void testJust() throws IOException {
        Observable<Integer> source = Observable.just(1);

        TestSubscriber<Integer> ts = new TestSubscriber<>();
        ts.requestMore(0);                                     // (1)

        source.unsafeSubscribe(ts);

        ts.getOnNextEvents().forEach(System.out::println);

        System.out.println("--");

        ts.unsubscribe();                                      // (2)

        ts.requestMore(1);
        source.unsafeSubscribe(ts);

        ts.getOnNextEvents().forEach(System.out::println);
    }

    @Test
    public void testSingleProducer() {
        class SingleProducer<T> extends AtomicBoolean implements Producer {             // (1)
            final Subscriber<? super T> child;                  // (2)
            final T value;
            public SingleProducer(Subscriber<? super T> child, T value) {
                this.child = child;
                this.value = value;
            }
            @Override
            public void request(long n) {
                if (n < 0) {
                    throw new IllegalArgumentException();    // (1)
                }
                if (n > 0 && compareAndSet(false, true)) {   // (2)
                    if (!child.isUnsubscribed()) {
                        child.onNext(value);                 // (3)
                    }
                    if (!child.isUnsubscribed()) {
                        child.onCompleted();                 // (4)
                    }
                }
            }
        }

        Integer value = 1;
        Observable<Integer> just = Observable.create(child -> {
            child.setProducer(new SingleProducer<>(child, value));            // (3)
        });
        just.subscribe(System.out::println);
    }

    @Test
    public void testSingleDelayProducer() throws InterruptedException {
        class SingleDelayedProducer<T> extends AtomicInteger implements Producer {
            private static final long serialVersionUID = 1L;
            final Subscriber<? super T> child;
            T value;                                                        // (1)
            static final int NO_REQUEST_NO_VALUE = 0;
            static final int NO_REQUEST_HAS_VALUE = 1;
            static final int HAS_REQUEST_NO_VALUE = 2;
            static final int HAS_REQUEST_HAS_VALUE = 3;

            public SingleDelayedProducer(Subscriber<? super T> child) {
                this.child = child;
            }

            @Override
            public void request(long n) {
                if (n < 0) {
                    throw new IllegalArgumentException();        // (1)
                }
                if (n == 0) {
                    return;
                }
                for (;;) {                                       // (2)
                    int s = get();
                    if (s == NO_REQUEST_NO_VALUE) {              // (3)
                        if (!compareAndSet(NO_REQUEST_NO_VALUE, HAS_REQUEST_NO_VALUE)) {
                            continue;                            // (4)
                        }
                    } else if (s == NO_REQUEST_HAS_VALUE) {      // (5)
                        if (compareAndSet(NO_REQUEST_HAS_VALUE, HAS_REQUEST_HAS_VALUE)) {
                            if (!child.isUnsubscribed()) {       // (6)
                                child.onNext(value);
                            }
                            if (!child.isUnsubscribed()) {
                                child.onCompleted();
                            }
                        }                                        // (7)
                    }
                    return;                                      // (8)
                }
            }

            public void setValue(T value) {
                System.out.println("========================");
                for (;;) {                                       // (1)
                    int s = get();
                    if (s == NO_REQUEST_NO_VALUE) {
                        this.value = value;                      // (2)
                        if (!compareAndSet(NO_REQUEST_NO_VALUE, NO_REQUEST_HAS_VALUE)) {
                            continue;
                        }
                    } else if (s == HAS_REQUEST_NO_VALUE) {      // (4)
                        if (compareAndSet(HAS_REQUEST_NO_VALUE, HAS_REQUEST_HAS_VALUE)) {
                            if (!child.isUnsubscribed()) {
                                child.onNext(value);
                            }
                            if (!child.isUnsubscribed()) {
                                child.onCompleted();
                            }
                        }
                    }
                    return;                                      // (5)
                }
            }
        }

        Observable<Integer> justDelayed = Observable.create(child -> {
            SingleDelayedProducer<Integer> producer = new SingleDelayedProducer<>(child);
            ForkJoinPool.commonPool().submit(() -> {
                System.out.println("-----------------");
                try {
                    Thread.sleep(500);                                     // (2)
                } catch (InterruptedException ex) {
                    child.onError(ex);
                    return;
                }
                producer.setValue(123);                                                  // (3)
                System.out.println("*********************");
            });
            child.setProducer(producer);
        });

        justDelayed.subscribe(System.out::println);

        Thread.sleep(2000);
    }
}
