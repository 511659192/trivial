package com.ym.rxJava.lift;

import rx.*;
import rx.internal.operators.BackpressureUtils;
import rx.internal.util.atomic.SpscLinkedAtomicQueue;
import rx.subjects.Subject;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by cdyangmeng on 2017/10/20.
 */
public class UnicastSubjectTest {

    static class UnicastSubject<T> extends Subject<T, T> {

        public static <T> UnicastSubject<T> create() {   // (1)
            State<T> state = new State<>();
            return new UnicastSubject<>(state);
        }

        final State<T> state;                            // (2)

        protected UnicastSubject(State<T> state) {       // (3)
            super(state);
            this.state = state;
        }

        @Override
        public void onNext(T t) {
            state.onNext(t);
        }

        @Override
        public void onError(Throwable e) {
            state.onError(e);
        }

        @Override
        public void onCompleted() {
            state.onCompleted();
        }

        @Override
        public boolean hasObservers() {
            return state.child != null;
        }
    }

    static class State<T> implements Observable.OnSubscribe<T>, Observer<T>, Producer, Subscription {                     // (4)
        volatile Subscriber<? super T> child;                  // (1)
        final AtomicBoolean once = new AtomicBoolean();        // (2)
        final Queue<T> queue = new SpscLinkedAtomicQueue<>();  // (3)
        volatile boolean done;                                 // (4)
        Throwable error;
        volatile boolean unsubscribed;                         // (5)
        final AtomicLong requested = new AtomicLong();         // (6)
        final AtomicInteger wip = new AtomicInteger();         // (7)

        @Override
        public void call(Subscriber<? super T> t) {
            if (!once.get() && once.compareAndSet(false, true)) {  // (1)
                t.add(this);
                t.setProducer(this);                               // (2)
                child = t;
                drain();                                           // (3)
            } else {
                if (done) {                                        // (4)
                    Throwable e = error;
                    if (e != null) {                               // (5)
                        t.onError(e);
                    } else {
                        t.onCompleted();
                    }
                } else {
                    t.onError(new IllegalStateException("Only one subscriber allowed."));          // (6)
                }
            }
        }

//        @Override
//        public void onNext(T t) {
//            if (done || unsubscribed) {
//                return;
//            }
//            queue.offer(t);
//            drain();
//        }

        @Override
        public void onNext(T t) {
            if (done || unsubscribed) {
                return;
            }
            if (wip.get() == 0 && wip.compareAndSet(0, 1)) {  // (1)
                long r = requested.get();                     // (2)
                if (r != 0 && queue.isEmpty()) {              // (3)
                    child.onNext(t);
                    if (r != Long.MAX_VALUE) {                // (4)
                        requested.decrementAndGet();
                    }
                    if (wip.decrementAndGet() == 0) {         // (5)
                        return;
                    }
                } else {
                    queue.offer(t);                           // (6)
                }
            } else {
                queue.offer(t);                               // (7)
                if (wip.getAndIncrement() != 0) {
                    return;
                }
            }
            drainLoop();                                      // (8)
        }
        @Override
        public void onError(Throwable e) {
            if (done || unsubscribed) {
                return;
            }
            error = e;
            done = true;
            drain();
        }

        @Override
        public void onCompleted() {
            if (done || unsubscribed) {
                return;
            }
            done = true;
            drain();
        }

        @Override
        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required");
            }
            if (n > 0) {
                BackpressureUtils.getAndAddRequest(requested, n);      // (1)
                drain();
            }
        }

        @Override
        public boolean isUnsubscribed() {
            return unsubscribed;
        }

        @Override
        public void unsubscribe() {
            if (!unsubscribed) {
                unsubscribed = true;
                if (wip.getAndIncrement() == 0) {                      // (2)
                    clear();
                }
            }
        }

        void clear() {
            queue.clear();
            child = null;
        }

        void drain() {
            if (wip.getAndIncrement() == 0) {
                drainLoop();
            }
        }
        void drainLoop() {
            int missed = 1;                                           // (1)

            final Queue<T> q = queue;
            Subscriber<? super T> child = this.child;                 // (2)

            for (;;) {

                if (child != null) {                                  // (3)

                    if (checkTerminated(done, q.isEmpty(), child)) {  // (4)
                        return;
                    }

                    long r = requested.get();
                    boolean unbounded = r == Long.MAX_VALUE;
                    long e = 0L;                                      // (5)

                    while (r != 0L) {
                        boolean d = done;
                        T v = q.poll();
                        boolean empty = v == null;                    // (6)

                        if (checkTerminated(d, empty, child)) {
                            return;
                        }

                        if (empty) {
                            break;
                        }

                        child.onNext(v);

                        r--;
                        e--;                                          // (7)
                    }

                    if (e != 0) {
                        if (!unbounded) {
                            requested.addAndGet(e);                   // (8)
                        }
                    }
                }

                missed = wip.addAndGet(-missed);                      // (9)
                if (missed == 0) {
                    return;
                }

                if (child == null) {                                  // (10)
                    child = this.child;
                }
            }
        }
        boolean checkTerminated(boolean done, boolean empty,
                                Subscriber<? super T> child) {
            if (unsubscribed) {                              // (1)
                clear();
                return true;
            }
            if (done && empty) {                             // (2)
                unsubscribed = true;
                this.child = null;
                Throwable e = error;
                if (e != null) {
                    child.onError(e);
                } else {
                    child.onCompleted();
                }
                return true;
            }
            return false;
        }

    }
}
