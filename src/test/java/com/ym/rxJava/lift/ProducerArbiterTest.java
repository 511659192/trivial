package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.observers.TestSubscriber;

/**
 * Created by cdyangmeng on 2017/10/7.
 */
public class ProducerArbiterTest {
    @Test
    public void testThen() {
        class ThenObserve<T> implements Observable.Operator<T, T> {
            final Observable<? extends T> other;
            public ThenObserve(Observable<? extends T> other) {
                this.other = other;
            }

            @Override
            public Subscriber<? super T> call(Subscriber<? super T> child) {
                Subscriber<T> parent = new Subscriber<T>(child, false) {
                    @Override
                    public void onNext(T t) {
                        child.onNext(t);
                    }
                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }
                    @Override
                    public void onCompleted() {
                        other.unsafeSubscribe(child);
                    }
                };
                child.add(parent);
                return parent;
            }
        }

        Observable<Integer> source = Observable
                .range(1, 10)
                .lift(new ThenObserve<>(Observable.range(11, 90)));

//        source.take(20).subscribe(System.out::println);
//        System.out.println("---");

        TestSubscriber<Integer> ts = new TestSubscriber<>();
        ts.requestMore(20);

        source.subscribe(ts);

        ts.getOnNextEvents().forEach(System.out::println);
    }

    @Test
    public void testArbiter() {
        class ProducerArbiter implements Producer {
            long requested;                                   // (1)
            Producer currentProducer;

            boolean emitting;                                 // (2)
            long missedRequested;
            long missedProduced;
            Producer missedProducer;

            final Producer NULL_PRODUCER = n -> { };   // (3)

            @Override
            public void request(long n) {
                if (n < 0) {                                   // (1)
                    throw new IllegalArgumentException();
                }
                if (n == 0) {
                    return;
                }
                synchronized (this) {
                    if (emitting) {
                        missedRequested += n;                  // (2)
                        return;
                    }
                    emitting = true;
                }
                boolean skipFinal = false;
                try {
                    long r = requested;
                    long u = r + n;
                    if (u < 0) {
                        u = Long.MAX_VALUE;
                    }
                    requested = u;                             // (3)

                    Producer p = currentProducer;
                    if (p != null) {
                        p.request(n);                          // (4)
                    }

                    emitLoop();                                // (5)
                    skipFinal = true;
                } finally {
                    if (!skipFinal) {                          // (6)
                        synchronized (this) {
                            emitting = false;
                        }
                    }
                }
            }

            public void produced(long n) {
                if (n <= 0) {                                    // (1)
                    throw new IllegalArgumentException();
                }
                synchronized (this) {
                    if (emitting) {
                        missedProduced += n;                     // (2)
                        return;
                    }
                    emitting = true;
                }

                boolean skipFinal = false;
                try {
                    long r = requested;
                    long u = r - n;
                    if (u < 0) {
                        throw new IllegalStateException();       // (3)
                    }
                    requested = u;

                    emitLoop();                                  // (4)
                    skipFinal = true;
                } finally {
                    if (!skipFinal) {
                        synchronized (this) {
                            emitting = false;
                        }
                    }
                }
            }

            public void set(Producer newProducer) {
                synchronized (this) {
                    if (emitting) {
                        missedProducer = newProducer == null ? NULL_PRODUCER : newProducer;          // (1)
                        return;
                    }
                    emitting = true;
                }
                boolean skipFinal = false;
                try {
                    currentProducer = newProducer;
                    if (newProducer != null) {
                        newProducer.request(requested);           // (2)
                    }

                    emitLoop();                                   // (3)
                    skipFinal = true;
                } finally {
                    if (!skipFinal) {
                        synchronized (this) {
                            emitting = false;
                        }
                    }
                }
            }

            public void emitLoop() {
                for (;;) {
                    long localRequested;
                    long localProduced;
                    Producer localProducer;
                    synchronized (this) {
                        localRequested = missedRequested;
                        localProduced = missedProduced;
                        localProducer = missedProducer;
                        if (localRequested == 0L && localProduced == 0L && localProducer == null) {       // (1)
                            emitting = false;
                            return;
                        }
                        missedRequested = 0L;
                        missedProduced = 0L;
                        missedProducer = null;                    // (2)
                    }

                    long r = requested;

                    if (r != Long.MAX_VALUE) {                    // (3)
                        long u = r + localRequested;
                        if (u < 0 || u == Long.MAX_VALUE) {       // (4)
                            r = Long.MAX_VALUE;
                            requested = r;
                        } else {
                            long v = u - localProduced;           // (5)
                            if (v < 0) {
                                throw new IllegalStateException();
                            }
                            r = v;
                            requested = v;
                        }
                    }
                    if (localProducer != null) {                  // (6)
                        if (localProducer == NULL_PRODUCER) {
                            currentProducer = null;
                        } else {
                            currentProducer = localProducer;
                            localProducer.request(r);             // (7)
                        }
                    } else {
                        Producer p = currentProducer;
                        if (p != null && localRequested != 0L) {
                            p.request(localRequested);            // (8)
                        }
                    }
                }
            }
        }

        class ThenObserve<T> implements Observable.Operator<T, T> {
            final Observable<? extends T> other;

            public ThenObserve(Observable<? extends T> other) {
                this.other = other;
            }

            @Override
            public Subscriber<? super T> call(Subscriber<? super T> child) {
                ProducerArbiter pa = new ProducerArbiter();         // (1)

                Subscriber<T> parent = new Subscriber<T>() {
                    @Override
                    public void onNext(T t) {
                        child.onNext(t);
                        pa.produced(1);                             // (2)
                    }

                    @Override
                    public void onError(Throwable e) {
                        pa.set(null);                               // (3)
                        child.onError(e);
                    }

                    @Override
                    public void onCompleted() {
                        pa.set(null);                               // (4)

                        Subscriber<T> parent2 = create2(pa, child); // (5)
                        child.add(parent2);

                        other.unsafeSubscribe(parent2);             // (6)
                    }

                    @Override
                    public void setProducer(Producer producer) {
                        pa.set(producer);                           // (7)
                    }
                };
                child.add(parent);
                child.setProducer(pa);
                return parent;
            }

            Subscriber<T> create2(ProducerArbiter pa, Subscriber<? super T> child) {                  // (8)
                return new Subscriber<T>() {
                    @Override
                    public void onNext(T t) {
                        child.onNext(t);
                        pa.produced(1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        pa.set(null);
                        child.onError(e);
                    }

                    @Override
                    public void onCompleted() {
                        pa.set(null);
                        child.onCompleted();
                    }

                    @Override
                    public void setProducer(Producer producer) {
                        pa.set(producer);
                    }
                };
            }
        }

        Observable<Integer> source = Observable
                .range(1, 10)
                .lift(new ThenObserve<>(Observable.range(11, 90)));

//        source.take(20).subscribe(System.out::println);
//        System.out.println("---");

        TestSubscriber<Integer> ts = new TestSubscriber<>();
        ts.requestMore(20);

        source.subscribe(ts);

        ts.getOnNextEvents().forEach(System.out::println);
    }
}
