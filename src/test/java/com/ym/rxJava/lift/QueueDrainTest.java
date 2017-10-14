package com.ym.rxJava.lift;

import rx.internal.util.unsafe.MpscLinkedQueue;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Created by cdyangmeng on 2017/9/30.
 * 队列漏
 * 并发比较多的场景适用
 */
public class QueueDrainTest {

    class BasicQueueDrain {
        final AtomicInteger wip = new AtomicInteger();  // (1)
        // 记录需要被执行的任务数量
        public void drain() {
            // work preparation
            if (wip.getAndIncrement() == 0) {           // (2)
                do {
                    // work draining
                } while (wip.decrementAndGet() != 0);   // (3)
            }
        }
    }

    class ValueQueueDrain<T> {
        final Queue<T> queue = new MpscLinkedQueue<>();     // (1)
        final AtomicInteger wip = new AtomicInteger();
        Consumer consumer;                                  // (2)

        public void drain(T value) {
            queue.offer(Objects.requireNonNull(value));     // (3)
            if (wip.getAndIncrement() == 0) {
                do {
                    T v = queue.poll();                     // (4)
                    consumer.accept(v);                     // (5)
                } while (wip.decrementAndGet() != 0);       // (6)
            }
        }
    }

    class ValueQueueDrainFastpath<T> {
        final Queue<T> queue = new MpscLinkedQueue<>();
        final AtomicInteger wip = new AtomicInteger();
        Consumer consumer;

        public void drain(T value) {
            Objects.requireNonNull(value);
            if (wip.compareAndSet(0, 1)) {          // (1)
                // （如果我们成功把 wip 从 0 变为 1）我们直接把数据传递给 consumer
                consumer.accept(value);             // (2)
                if (wip.decrementAndGet() == 0) {   // (3)
                    return;
                }
            } else {
                queue.offer(value);                 // (4)
                if (wip.getAndIncrement() != 0) {   // (5)
                    return;
                }
            }
            do {
                T v = queue.poll();                 // (6)
                // 漏循环
                consumer.accept(v);
            } while (wip.decrementAndGet() != 0);
        }
    }

    class ValueQueueDrainOptimized<T> {
        final Queue<T> queue = new MpscLinkedQueue<>();
        final AtomicInteger wip = new AtomicInteger();
        Consumer consumer;

        public void drain(T value) {
            queue.offer(Objects.requireNonNull(value));
            if (wip.getAndIncrement() == 0) {
                do {
                    wip.set(1);                              // (1)
                    T v;
                    while ((v = queue.poll()) != null) {     // (2)
                        consumer.accept(v);
                    }
                } while (wip.decrementAndGet() != 0);        // (3)
            }
        }
    }
}
