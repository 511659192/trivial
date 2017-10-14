package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Subscription;
import rx.exceptions.Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by cdyangmeng on 2017/10/13.
 */
public class TwoSubscribersLockFreeTest {

    @Test
    public void testTwoSubscribersLockFree1() {
        class TwoSubscribersLockFree1 implements Subscription {
            class State {
                final Subscription s1;
                final Subscription s2;
                final boolean isUnsubscribed;
                public State(Subscription s1, Subscription s2, boolean isUnsubscribed) {
                    this.s1 = s1;
                    this.s2 = s2;
                    this.isUnsubscribed = isUnsubscribed;
                }
            }

            final State EMPTY = new State(null, null, false);                  // (1)
            final State UNSUBSCRIBED = new State(null, null, true);                   // (2)
            final AtomicReference<State> state = new AtomicReference<>(EMPTY);                  // (3)

            public void set(boolean first, Subscription s) {
                for (;;) {
                    State current = state.get();                    // (1)
                    if (current.isUnsubscribed) {                   // (2)
                        s.unsubscribe();
                        return;
                    }
                    State next;
                    Subscription old;
                    if (first) {
                        next = new State(s, current.s2, false);     // (3)  基于当前状态创建一个新的状态，替换相应的 subscription
                        old = current.s1;                           // (4) 局部变量保存被替换的 subscription
                    } else {
                        next = new State(current.s1, s, false);
                        old = current.s2;
                    }
                    // 通过 CAS 操作来切换新旧状态，如果失败，说明当前有并发线程成功修改了状态，继续循环进行尝试
                    if (state.compareAndSet(current, next)) {       // (5)
                        if (old != null) {
                            old.unsubscribe();                      // (6)
                        }
                        return;
                    }
                }
            }

            @Override
            public void unsubscribe() {
                State current = state.get();                        // (1)
                if (!current.isUnsubscribed) {                      // (2)
                    current = state.getAndSet(UNSUBSCRIBED);        // (3)
                    if (!current.isUnsubscribed) {                  // (4)
                        List<Throwable> errors = null;              // (5)
                        errors = unsubscribe(current.s1, errors);   // (6)
                        errors = unsubscribe(current.s2, errors);
                        Exceptions.throwIfAny(errors);              // (7)
                    }
                }
            }

            private List<Throwable> unsubscribe(Subscription s, List<Throwable> errors) {
                if (s != null) {
                    try {
                        s.unsubscribe();
                    } catch (Throwable e) {
                        if (errors == null) {
                            errors = new ArrayList<>();
                        }
                        errors.add(e);
                    }
                }
                return errors;
            }

            @Override
            public boolean isUnsubscribed() {
                return state.get().isUnsubscribed;
            }
        }
    }

    @Test
    public void testTwoSubscribersLockFree2() {
        class TwoSubscribersLockFree2 implements Subscription {
            class State {
                final Subscription s1;
                final Subscription s2;

                public State(Subscription s1, Subscription s2) {
                    this.s1 = s1;
                    this.s2 = s2;

                }
            }
            final State EMPTY = new State(null, null);         // (1)
            final State UNSUBSCRIBED = new State(null, null);
            final AtomicReference<State> state = new AtomicReference<State>(EMPTY);

            public void set(boolean first, Subscription s) {
                for (;;) {
                    State current = state.get();
                    if (current == UNSUBSCRIBED) {                    // (2)
                        s.unsubscribe();
                        return;
                    }
                    State next;
                    Subscription old;
                    if (first) {
                        next = new State(s, current.s2);
                        old = current.s1;
                    } else {
                        next = new State(current.s1, s);
                        old = current.s2;
                    }
                    if (state.compareAndSet(current, next)) {
                        if (old != null) {
                            old.unsubscribe();
                        }
                        return;
                    }
                }
            }

            @Override
            public boolean isUnsubscribed() {
                return state.get() == UNSUBSCRIBED;                    // (3)
            }

            @Override
            public void unsubscribe() {
                State current = state.get();
                if (current != UNSUBSCRIBED) {                         // (4)
                    current = state.getAndSet(UNSUBSCRIBED);
                    if (current != UNSUBSCRIBED) {                     // (5)
                        List<Throwable> errors = null;
                        errors = unsubscribe(current.s1, errors);
                        errors = unsubscribe(current.s2, errors);
                        Exceptions.throwIfAny(errors);
                    }
                }
            }
            private List<Throwable> unsubscribe(Subscription s, List<Throwable> errors) {
                if (s != null) {
                    try {
                        s.unsubscribe();
                    } catch (Throwable e) {
                        if (errors == null) {
                            errors = new ArrayList<>();
                        }
                        errors.add(e);
                    }
                }
                return errors;
            }

        }
    }
}
