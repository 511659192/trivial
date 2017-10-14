package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.internal.operators.BackpressureUtils;
import rx.internal.producers.QueuedProducer;
import rx.internal.util.unsafe.SpscLinkedQueue;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by cdyangmeng on 2017/10/3.
 */
public class QueuedProducerTest {

    @Test
    public void testQueneProducer() {
        class QueuedProducer<T> extends AtomicLong implements Producer {
            private static final long serialVersionUID = -1;

            final Subscriber<? super T> child;
            final Queue<T> queue;
            final AtomicInteger wip;

            public QueuedProducer(Subscriber<? super T> child) {
                this.child = child;
                this.queue = new SpscLinkedQueue<>();
                this.wip = new AtomicInteger();
            }

            @Override
            public void request(long n) {                              // (1)
                if (n < 0) {
                    throw new IllegalArgumentException();   // (1)
                }
                if (n > 0) {
                    BackpressureUtils.getAndAddRequest(this, n);         // (2)
                    drain();                                // (3)
                }
            }

            public void offer(T value) {                               // (2)
                queue.offer(Objects.requireNonNull(value));  // (1)
                drain();                                     // (2)
            }

            private void drain() {                                     // (3)
                System.out.println("-------------------------------");
                if (wip.getAndIncrement() == 0) {                // (1)
                    do {
                        if (child.isUnsubscribed()) {            // (2)
                            return;
                        }

                        wip.lazySet(1);                          // (3)

                        long r = get();                          // (4)
                        long e = 0;
                        T v;

                        while (r != 0 &&  (v = queue.poll()) != null) {    // (6)
                            child.onNext(v);
                            if (child.isUnsubscribed()) {        // (7)
                                return;
                            }
                            r--;
                            e++;
                        }

                        if (e != 0) {                            // (8)
                            addAndGet(-e);
                        }
                    } while (wip.decrementAndGet() != 0);        // (9)
                }
            }
        }

        Observable<Integer> source = Observable.create(child -> {
            QueuedProducer<Integer> qp = new QueuedProducer<>(child);
            for (int i = 0; i < 200; i++) {
                qp.offer(i);
            }
            child.setProducer(qp);
        });

        source.take(150).subscribe(System.out::println);
    }

    @Test
    public void testFullQueuedProducer() {
        class FullQueuedProducer<T> extends AtomicLong implements Producer, Observer<T> {
            private static final long serialVersionUID = -1L;

            final Subscriber child;
            final Queue<T> queue;
            final AtomicInteger wip;

            Throwable error;                                         // (1)
            volatile boolean done;                                   // (2)

            public FullQueuedProducer(Subscriber child) {
                this.child = child;
                this.queue = new SpscLinkedQueue<>();
                this.wip = new AtomicInteger();
            }

            @Override
            public void request(long n) {
                if (n < 0) {
                    throw new IllegalArgumentException();
                }
                if (n > 0) {
                    BackpressureUtils.getAndAddRequest(this, n);
                    drain();
                }
            }

            @Override
            public void onNext(T value) {                             // (3)
                queue.offer(Objects.requireNonNull(value));
                drain();
            }

            @Override
            public void onError(Throwable e) {                        // (4)
                error = e;
                done = true;
                drain();
            }

            @Override
            public void onCompleted() {                               // (5)
                done = true;
                drain();
            }

            private boolean checkTerminated(boolean isDone, boolean isEmpty) {
                if (child.isUnsubscribed()) {                  // (1)
                    return true;
                }
                if (isDone) {                                  // (2)
                    Throwable e = error;
                    if (e != null) {                           // (3)
                        queue.clear();
                        child.onError(e);
                        return true;
                    } else if (isEmpty) {                      // (4)
                        child.onCompleted();
                        return true;
                    }
                }
                return false;                                  // (5)
            }

            private void drain() {
                if (wip.getAndIncrement() == 0) {
                    do {
                        if (checkTerminated(done, queue.isEmpty())) {    // (1)
                            return;
                        }

                        wip.lazySet(1);

                        long r = get();
                        long e = 0;

                        while (r != 0) {
                            boolean d = done;                            // (2)
                            T v = queue.poll();
                            if (checkTerminated(d, v == null)) {         // (3)
                                return;
                            } else if (v == null) {                      // (4)
                                break;
                            }

                            child.onNext(v);
                            r--;
                            e++;
                        }

                        if (e != 0) {
                            addAndGet(-e);
                        }
                    } while (wip.decrementAndGet() != 0);
                }
            }
        }


        Observable<Integer> source = Observable.create(child -> {
            FullQueuedProducer<Integer> producer = new FullQueuedProducer<>(child);
            for (int i = 0; i < 200; i++) {
                producer.onNext(i);
            }
            child.setProducer(producer);
        });

        source.take(150).subscribe(System.out::println);
    }
}
