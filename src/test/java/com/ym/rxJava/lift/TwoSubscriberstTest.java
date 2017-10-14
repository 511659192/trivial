package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Subscription;
import rx.exceptions.Exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cdyangmeng on 2017/10/13.
 */
public class TwoSubscriberstTest {

    @Test
    public void testTwoSubscribers() {
        class TwoSubscribers implements Subscription {
            private volatile boolean isUnsubscribed;          // (1)

            Subscription s1;                                  // (2)
            Subscription s2;

            @Override
            public boolean isUnsubscribed() {
                return isUnsubscribed;                        // (3)
            }

            public void set(boolean first, Subscription s) {
                if (!isUnsubscribed) {                       // (1)
                    synchronized (this) {
                        if (!isUnsubscribed) {               // (2)
                            Subscription temp;               // (3)
                            if (first) {
                                temp = s1;
                                s1 = s;
                            } else {
                                temp = s2;
                                s2 = s;
                            }
                            s = temp;                        // (4)
                        }
                    }
                }
                if (s != null) {                             // (5)
                    s.unsubscribe();
                }
            }

            @Override
            public void unsubscribe() {
                if (!isUnsubscribed) {                  // (1)
                    Subscription one;                   // (2)
                    Subscription two;
                    synchronized (this) {
                        if (isUnsubscribed) {           // (3)
                            return;
                        }

                        isUnsubscribed = true;          // (4)

                        one = s1;                       // (5)
                        two = s2;

                        s1 = null;
                        s2 = null;
                    }

                    List<Throwable> errors = null;      // (6)
                    try {
                        if (one != null) {
                            one.unsubscribe();          // (7)
                        }
                    } catch (Throwable t) {
                        errors = new ArrayList<>();     // (8)
                        errors.add(t);
                    }
                    try {
                        if (two != null) {
                            two.unsubscribe();
                        }
                    } catch (Throwable t) {
                        if (errors == null) {
                            errors = new ArrayList<>();
                        }
                        errors.add(t);
                    }

                    Exceptions.throwIfAny(errors);      // (9)
                }
            }
        }
    }
}
