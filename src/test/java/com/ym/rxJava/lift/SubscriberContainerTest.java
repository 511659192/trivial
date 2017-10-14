package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Subscriber;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by cdyangmeng on 2017/10/14.
 */
// 能添加和移除某种 subscription，例如 Subscriber；
// 能获取当前的内容（包含的 subscription）；
// 能够在不取消订阅其包含的 subscription 的前提下，取消订阅容器类；
// 添加 subscription 能得知是否成功；
public class SubscriberContainerTest {

    @Test
    public void testSubscriberContainer() {
        class SubscriberContainer<T> {
            Subscriber[] EMPTY = new Subscriber[0];     // (2)
            Subscriber[] TERMINATED = new Subscriber[0];

            final AtomicReference<Subscriber[]> array = new AtomicReference<>(EMPTY);                      // (3)

            public Subscriber<T>[] get() {                           // (4)
                return array.get();
            }

            public boolean add(Subscriber<T> s) {                    // (5)
                for (;;) {
                    Subscriber[] current = array.get();
                    if (current == TERMINATED) {                  // (1)
                        return false;
                    }
                    int n = current.length;
                    Subscriber[] next = new Subscriber[n + 1];
                    System.arraycopy(current, 0, next, 0, n);     // (2)
                    next[n] = s;
                    if (array.compareAndSet(current, next)) {     // (3)
                        return true;
                    }
                }
            }

            public boolean remove(Subscriber<T> s) {                 // (6)
                for (;;) {
                    Subscriber[] current = array.get();
                    if (current == EMPTY || current == TERMINATED) {             // (1)
                        return false;
                    }
                    int len = current.length;
                    int index = -1; // 待移除的下标
                    for (int i = 0; i < len; i++) {                   // (2)
                        Subscriber e = current[i];
                        if (e.equals(s)) {
                            index = i;
                            break;
                        }
                        i++;
                    }
                    if (index < 0) {                                    // (3)
                        return false;
                    }
                    Subscriber[] next;
                    if (len == 1) {                                   // (4)
                        next = EMPTY;
                    } else {
                        next = new Subscriber[len - 1];
                        System.arraycopy(current, 0, next, 0, index);
                        System.arraycopy(current, index + 1, next, index, len - index - 1);                    // (5)
                    }
                    if (array.compareAndSet(current, next)) {       // (6)
                        return true;
                    }
                }
            }

            public Subscriber<T>[] getAndTerminate() {               // (7) 容器终结后 可以进行其他的操作
                return array.getAndSet(TERMINATED);
            }

            public boolean isTerminated() {                          // (8)
                return get() == TERMINATED;
            }
        }
    }
}
