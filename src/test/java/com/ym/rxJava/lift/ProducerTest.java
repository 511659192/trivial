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
public class ProducerTest {

    @Test
    public void test() {
        Observable<Integer> range = Observable.create(child -> {   // (1)
            int[] index = new int[] { 0 };                         // (2)
            Producer p = n -> {                                    // (3)
                int j = index[0];
                for (int i = 0; i < n; i++) {                       // (4)
                    child.onNext(j);
                    j++;
                    if (j == 100) {                                 // (5)
                        child.onCompleted();
                        return;
                    }
                }
                index[0] = j;                                       // (6)
            };

            child.setProducer(p);                                  // (7)
        });

        TestSubscriber<Integer> ts = new TestSubscriber<>();
        ts.requestMore(0);

        range.subscribe(ts);

        ts.requestMore(25);
        ts.getOnNextEvents().forEach(System.out::println);

        ts.requestMore(10);
        ts.getOnNextEvents().forEach(System.out::println);

        ts.requestMore(65);
        ts.getOnNextEvents().forEach(System.out::println);
    }



    @Test
    public void testRangeProducer() {
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
}
