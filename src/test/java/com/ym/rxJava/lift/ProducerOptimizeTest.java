package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.internal.operators.BackpressureUtils;
import rx.observers.TestSubscriber;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by cdyangmeng on 2017/9/30.
 */
public class ProducerOptimizeTest {

    @Test
    public void testRangeProducerOptimize() {
        class RangeProducer extends AtomicLong implements Producer {
            private static final long serialVersionUID = 1;

            final Subscriber<? super Integer> child;                  // (1)
            int index;                                                // (2)
            int remaining;                                            // (3)

            public RangeProducer(Subscriber<? super Integer> child, int start, int count) {
                if (count <= 0) {
                    throw new IllegalArgumentException();             // (4)
                }
                this.child = child;
                this.index = start;
                this.remaining = count;
            }

            @Override
            public void request(long n) {
                if (n < 0) {
                    throw new IllegalArgumentException();           // (1)
                }
                if (n == 0) {
                    return;                                         // (2)
                }

                /**
                 * 增加一个快路径
                 */
                if (BackpressureUtils.getAndAddRequest(this, n) != 0) {
                    return;
                }
                if (n == Long.MAX_VALUE) {                                // (1)
                    if (child.isUnsubscribed()) {
                        return;
                    }
                    int i = index;                                        // (2)
                    int k = remaining;
                    while (k != 0) {
                        child.onNext(i);
                        if (child.isUnsubscribed()) {                     // (3)
                            return;
                        }
                        i++;                                              // (4)
                        k--;
                    }
                    if (!child.isUnsubscribed()) {
                        child.onCompleted();
                    }
                    return;                                               // (5)
                }

                long r;
                for (;;) {
                    r = get();                                      // (3)
                    long u = r + n;                                 // (4)

                    if (u < 0) {
                        u = Long.MAX_VALUE;                         // (5)
                        // 加法过程可能发生溢出，我们保证总数量不超过 Long.MAX_VALUE，我们可以把它看做无穷大
                    }

                    if (compareAndSet(r, u)) {                      // (6)
                        break;
                    }
                }
            }
        }

        class RxRange implements Observable.OnSubscribe<Integer> {
            final int start;
            final int count;
            public RxRange(int start, int count) {
                if (count < 0) {
                    throw new IllegalArgumentException();
                }
                this.start = start;
                this.count = count;
            }
            @Override
            public void call(Subscriber t) {
                if (count == 0) {
                    t.onCompleted();
                    return;
                }
                RangeProducer p = new RangeProducer(t, start, count);
                t.setProducer(p);
            }

            public Observable<Integer> toObservable() {
                return Observable.create(this);
            }

            final class RangeProducer extends AtomicLong implements Producer {
                final Subscriber<? super Integer> child;
                int index;
                int remaining;
                public RangeProducer(Subscriber<? super Integer> child, int start, int count) {
                    this.child = child;
                    this.index = start;
                    this.remaining = count;
                }
                @Override
                public void request(long n) {
                    if (n < 0) {
                        throw new IllegalArgumentException();
                    }
                    if (n == 0) {
                        return;
                    }
                    if (BackpressureUtils.getAndAddRequest(this, n) != 0) {
                        return;
                    }
                    long r = n;
                    for (;;) {
                        if (child.isUnsubscribed()) {
                            return;
                        }
                        int i = index;
                        int k = remaining;
                        int e = 0;

                        while (r > 0 && k > 0) {
                            child.onNext(i);
                            if (child.isUnsubscribed()) {
                                return;
                            }
                            k--;
                            if (k == 0) {
                                child.onCompleted();
                                return;
                            }
                            e++;
                            i++;
                            r--;
                        }
                        index = i;
                        remaining = k;

                        r = addAndGet(-e);

                        if (r == 0) {
                            return;
                        }
                    }
                }
            }
        }


        Observable<Integer> range = new RxRange(1, 10).toObservable();

        range.take(5).subscribe(
                System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done")
        );
    }


    @Test
    public void testArrayProducer() {
        class ArrayProducer extends AtomicLong implements Producer {

            final Subscriber child;
            final int[] array;                                        // (1)
            int index;
            public ArrayProducer(Subscriber child, int[] array) {
                this.child = child;
                this.array = array;
            }
            @Override
            public void request(long n) {
                if (n < 0) {
                    throw new IllegalArgumentException();
                }
                if (n == 0) {
                    return;
                }
                if (BackpressureUtils.getAndAddRequest(this, n) != 0) {
                    return;
                }
                final int[] a = this.array;
                final int k = a.length;                               // (2)
                if (n == Long.MAX_VALUE) {
                    if (child.isUnsubscribed()) {
                        return;
                    }
                    int i = index;
                    while (i != k) {                                  // (3)
                        child.onNext(a[i]);
                        if (child.isUnsubscribed()) {
                            return;
                        }
                        i++;
                    }
                    if (!child.isUnsubscribed()) {
                        child.onCompleted();
                    }
                    return;
                }
                long r = n;
                for (;;) {
                    if (child.isUnsubscribed()) {
                        return;
                    }
                    int i = index;
                    int e = 0;

                    while (r > 0 && i != k) {
                        child.onNext(a[i]);
                        if (child.isUnsubscribed()) {
                            return;
                        }
                        i++;
                        if (i == k) {                               // (4)
                            child.onCompleted();
                            return;
                        }
                        e++;
                        r--;
                    }
                    index = i;

                    r = addAndGet(-e);

                    if (r == 0) {
                        return;
                    }
                }
            }
        }

        int[] array = new int[200];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        Observable<Integer> source = Observable.create(child -> {
            if (array.length == 0) {
                child.onCompleted();
                return;
            }
            ArrayProducer ap = new ArrayProducer(child, array);
            child.setProducer(ap);
        });
        source.subscribe(System.out::println);
    }
}
