package com.ym.rxJava.rs;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ym on 2017/10/18.
 */
public class NewSubscriptionTest {

    static class SingleSubscription<T> extends AtomicBoolean implements Subscription {
        private static final long serialVersionUID = 1L;

        final T value;                                       // (1)
        final Subscriber<? super T> child;
        volatile boolean cancelled;                          // (2)

        public SingleSubscription(T value, Subscriber<? super T> child) {               // (3)
            this.value = Objects.requireNonNull(value);
            this.child = Objects.requireNonNull(child);
        }
        @Override
        public void request(long n) {
            if (n <= 0) {
                throw new IllegalArgumentException("n > 0 required");                       // (4)
            }
            if (compareAndSet(false, true)) {
                if (!cancelled) {                            // (5)
                    child.onNext(value);
                    if (!cancelled) {
                        child.onComplete();
                    }
                }
            }
        }
        @Override
        public void cancel() {
            cancelled = true;                                // (6)
        }
    }

    static class SingleDelayedSubscription<T> extends AtomicInteger implements Subscription {
        private static final long serialVersionUID = -1L;

        T value;
        final Subscriber<? super T> child;

        static final int CANCELLED = -1;                           // (1)
        static final int NO_VALUE_NO_REQUEST = 0;
        static final int NO_VALUE_HAS_REQUEST = 1;
        static final int HAS_VALUE_NO_REQUEST = 2;
        static final int HAS_VALUE_HAS_REQUEST = 3;

        public SingleDelayedSubscription(Subscriber<? super T> child) {
            this.child = Objects.requireNonNull(child);
        }
        @Override
        public void request(long n) {
            if (n <= 0) {
                throw new IllegalArgumentException("n > 0 required");
            }
            for (;;) {
                int s = get();
                if (s == NO_VALUE_HAS_REQUEST || s == HAS_VALUE_HAS_REQUEST || s == CANCELLED) {                       // (2)
                    return;
                } else if (s == NO_VALUE_NO_REQUEST) {
                    if (!compareAndSet(s, NO_VALUE_HAS_REQUEST)) {
                        continue;
                    }
                } else if (s == HAS_VALUE_NO_REQUEST) {
                    if (compareAndSet(s, HAS_VALUE_HAS_REQUEST)) {
                        T v = value;
                        value = null;
                        child.onNext(v);
                        if (get() != CANCELLED) {                  // (3)
                            child.onComplete();
                        }
                    }
                }
                return;
            }
        }

        public void setValue(T value) {
            Objects.requireNonNull(value);
            for (;;) {
                int s = get();
                if (s == HAS_VALUE_NO_REQUEST || s == HAS_VALUE_HAS_REQUEST || s == CANCELLED) {                        // (4)
                    return;
                } else if (s == NO_VALUE_NO_REQUEST) {
                    this.value = value;
                    if (!compareAndSet(s, HAS_VALUE_NO_REQUEST)) {
                        continue;
                    }
                } else if (s == NO_VALUE_HAS_REQUEST) {
                    if (compareAndSet(s, HAS_VALUE_HAS_REQUEST)) {
                        child.onNext(value);
                        if (get() != CANCELLED) {                   // (5)
                            child.onComplete();
                        }
                    }
                }
                return;
            }
        }

        @Override
        public void cancel() {
            int state = get();
            if (state != CANCELLED) {                              // (6)
                state = getAndSet(CANCELLED);
                if (state != CANCELLED) {
                    value = null;
                }
            }
        }
    }

    static class RangeSubscription extends AtomicLong implements Subscription {
        private static final long serialVersionUID = 1L;

        final Subscriber<? super Integer> child;
        int index;
        final int max;

        static final long CANCELLED = Long.MIN_VALUE;          // (1)

        public RangeSubscription(Subscriber<? super Integer> child, int start, int count) {
            this.child = Objects.requireNonNull(child);
            this.index = start;
            this.max = start + count;
        }
        @Override
        public void request(long n) {
            if (n <= 0) {
                throw new IllegalArgumentException("n > required");
            }
            long r;
            for (;;) {
                r = get();
                if (r == CANCELLED) {                          // (2)
                    return;
                }
                long u = r + n;
                if (u < 0) {
                    u = Long.MAX_VALUE;
                }
                if (compareAndSet(r, u)) {
                    break;
                }
            }
            if (r != 0L) {                                     // (p1)
                return;
            }
            for (;;) {
                r = get();
                if (r == CANCELLED) {                          // (3)
                    return;
                }
                int i = index;
                int m = max;
                long e = 0;
                while (r > 0L && i < m) {                      // (p2)
                    child.onNext(i);
                    if (get() == CANCELLED) {                  // (4)
                        return;
                    }
                    i++;
                    if (i == m) {
                        child.onComplete();
                        return;
                    }
                    r--;
                    e++;
                }
                index = i;
                if (e != 0) {
                    for (;;) {
                        r = get();
                        if (r == CANCELLED) {                  // (5)
                            return;
                        }
                        long u = r - e;
                        if (u < 0) {
                            throw new IllegalStateException("more produced than requested!");
                        }
                        if (compareAndSet(r, u)) {
                            break;
                        }
                    }
                }
                if (r <= 0L) {                                 // (p3)
                    break;
                }
            }
        }
        @Override
        public void cancel() {
            if (get() != CANCELLED) {                          // (6)
                getAndSet(CANCELLED);
            }
        }
    }


}
