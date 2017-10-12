package com.ym.rxJava.lift;

import rx.internal.util.unsafe.MpscLinkedQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Created by cdyangmeng on 2017/9/30.
 * 发射者循环
 * 在大部分的单线程使用场景下，这种实现方式性能表现非常突出，因为 Java (JVM) 的 JIT 编译器会在检测到只有单线程使用时使用偏向锁和锁省略技术
 */
public class EmitterLoopSerializerTest {

    class EmitterLoopSerializer {
        boolean emitting;
        boolean missed;
        public void emit() {
            synchronized (this) {           // (1)
                //进入（1）时，可能存在两种状态：没有线程正在发射，或者有一个线程正在发射。
                // synchronized 代码块中，如果（4）处有一个线程正在进行发射操作，那这个线程必须等待我们退出 synchronized 代码块（1），它才能进入 synchronized 代码块（5）。
                if (emitting) {
                    missed = true;          // (2)
                    // 如果此时有一个线程正在发射，那我们就需要标记一下，它告诉当前的发射者，还有更多事件需要发射
                    return;
                }
                emitting = true;            // (3)
                // 如果此时没有线程正在发射，那当前线程就获得了执行发射操作的权利，它会把 emitting 置为 true
            }
            for (;;) {
                // do all emission work     // (4)
                // 当一个线程获得执行发射操作的权利之后，我们就进入到了发射循环，把这个线程能看到的所有需要发射的事件都发射出去
                synchronized (this) {       // (5)
                    // 所有的发射任务执行完毕之后，它会进入 synchronized 代码块（5）。
                    if (!missed) {          // (6)
                        emitting = false;
                        return;
                    }
                    missed = false;         // (7)
                    // 在发射者循环中，如果有更多的事件需要发射，我们会重置 missed 变量的值，然后重新开始循环。重置 missed 非常关键，否则将会导致死循环。
                }
            }
        }
    }

    /**
     * 使用具备线程安全性的数据结构来作为事件队列，因此对事件队列的访问就无需加锁了。
     * @param <T>
     */
    class ValueEmitterLoop<T> {
        Queue<T> queue = new MpscLinkedQueue<>();    // (1)
        // 可能会有很多个线程调用 emit 函数（向队列中添加数据），而只会有一个线程从队列中取出数据，所以 Java 的 ConcurrentLinkedQueue 会有不必要的性能开销
        boolean emitting;
        Consumer<? super T> consumer;                // (2)

        public void emit(T value) {
            Objects.requireNonNull(value);
            queue.offer(value);                      // (3)
            // 添加非 null 的数据
            synchronized (this) {
                if (emitting) {
                    return;                          // (4)
                }
                emitting = true;
            }
            for (;;) {
                T v = queue.poll();                  // (5)
                if (v != null) {
                    consumer.accept(v);              // (6)
                    // 队列中不允许有 null，所以从队列中取出 null 就意味着队列已经空
                } else {
                    synchronized (this) {
                        if (queue.isEmpty()) {       // (7)
                            emitting = false;
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 在 synchronized 代码块中对队列进行操作，所以我们可以使用非线程安全的数据结构
     * @param <T>
     */
    class ValueListEmitterLoop<T> {
        List<T> queue;                           // (1)
        boolean emitting;
        Consumer<? super T> consumer;

        public void emit(T value) {
            synchronized (this) {
                if (emitting) {
                    List<T> q = queue;
                    if (q == null) {
                        q = new ArrayList<>();   // (2)
                        queue = q;
                    }
                    q.add(value);
                    return;
                }
                emitting = true;
            }
            boolean skipFinal = false;
            // 如果为 true，就表明我们正常退出循环，并跳过 finally 语句中的逻辑。
            try {
                consumer.accept(value);            // (5)
                for (;;) {
                    List<T> q;
                    synchronized (this) {
                        q = queue;
                        if (q == null) {
                            emitting = false;
                            skipFinal = true;      // (2)
                            return;
                        }
                        queue = null;
                    }
                    q.forEach(consumer);           // (6)
                }
            } finally {
                if (!skipFinal) {                  // (3)
                    synchronized (this) {
                        emitting = false;          // (4)
                    }
                }
            }
        }
    }
}
