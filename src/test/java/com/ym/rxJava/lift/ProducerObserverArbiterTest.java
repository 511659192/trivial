package com.ym.rxJava.lift;

import com.ym.rxJava.PrintSubscriber;
import org.junit.Test;
import rx.*;
import rx.schedulers.Schedulers;
import rx.subscriptions.SerialSubscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/10/9.
 */
public class ProducerObserverArbiterTest {

    @Test
    public void testProducerObserverArbiter() throws Exception {
        class ErrorSentinel {                     // (1)
            final Throwable error;
            public ErrorSentinel(Throwable error) {
                this.error = error;
            }
        }

        // 用一个常量对象来表示 onCompleted 已经发生
        final Object COMPLETED_SENTINEL = new Object(); // (2)

        // 正的请求量和负的生产量
        class RequestSentinel {                   // (3)
            final long n;
            public RequestSentinel(long n) {
                this.n = n;
            }
        }

        // 切换或者清除 producer
        class ProducerSentinel {                  // (4)
            final Producer p;
            public ProducerSentinel(Producer p) {
                this.p = p;
            }
        }

        class ProducerObserverArbiter<T> implements Producer, Observer<T> {      // (1)
            final Subscriber child;

            boolean emitting;

            List<Object> queue;                 // (2)
            Producer currentProducer;
            long requested;

            public ProducerObserverArbiter(Subscriber<? super T> child) {
                this.child = child;
            }

            @Override
            public void onNext(T t) {
                synchronized (this) {
                    if (emitting) {
                        List<Object> q = queue;
                        if (q == null) {
                            q = new ArrayList<>();
                            queue = q;
                        }
                        q.add(t);
                        return;
                    }
                    emitting = true;
                }
                boolean skipFinal = false;
                try {
                    child.onNext(t);

                    long r = requested;
                    // 非无限模式下递减了当前的总请求量（因为我们立即就执行了一次 child.onNext()）
                    if (r != Long.MAX_VALUE) { // (1)
                        requested = r - 1;
                    }

                    emitLoop();
                    skipFinal = true;
                } finally {
                    if (!skipFinal) {
                        synchronized (this) {
                            emitting = false;
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                synchronized (this) {
                    if (emitting) {
                        List<Object> q = new ArrayList<>();
                        // 清空队列再加入（1），所以 emitLoop() 将会跳过老的其他事件，优先处理 onError
                        q.add(new ErrorSentinel(e));        // (1)
                        queue = q;
                        return;
                    }
                    emitting = true;
                }
                child.onError(e);                           // (2)
            }

            @Override
            public void onCompleted() {
                synchronized (this) {
                    if (emitting) {
                        List<Object> q = new ArrayList<>();
                        q.add(COMPLETED_SENTINEL);
                        queue = q;
                        return;
                    }
                    emitting = true;
                }
                child.onCompleted();
            }

            @Override
            public void request(long n) {
                System.out.println("===========" + n);
                if (n < 0) {
                    throw new IllegalArgumentException();
                }
                if (n == 0) {
                    return;
                }
                synchronized (this) {
                    if (emitting) {
                        List<Object> q = queue;
                        if (q == null) {
                            q = new ArrayList<>();
                            queue = q;
                        }
                        q.add(new RequestSentinel(n));          // (1)
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
                    requested = u;                             // (2)

                    Producer p = currentProducer;
                    if (p != null) {                           // (3)
                        p.request(n);
                    }
                    emitLoop();
                    skipFinal = true;
                } finally {
                    if (!skipFinal) {
                        synchronized (this) {
                            emitting = false;
                        }
                    }
                }
            }

            public void set(Producer p) {
                System.out.println("--------------" + p);
                synchronized (this) {
                    if (emitting) {
                        List<Object> q = queue;
                        if (q == null) {
                            q = new ArrayList<>();
                            queue = q;
                        }
                        q.add(new ProducerSentinel(p));
                        return;
                    }
                    emitting = true;
                }
                boolean skipFinal = false;
                try {
                    currentProducer = p;
                    long r = requested;
                    if (p != null && r != 0) {                  // (1)
                        p.request(r);
                    }
                    emitLoop();
                    skipFinal = true;
                } finally {
                    if (!skipFinal) {
                        synchronized (this) {
                            emitting = false;
                        }
                    }
                }
            }

            void emitLoop() {
                for (;;) {
                    List<Object> q;
                    // 一次性把队列中的数据取出来
                    synchronized (this) {                                    // (1)
                        q = queue;
                        if (q == null) {
                            emitting = false;
                            return;
                        }
                        queue = null;
                    }
                    long e = 0;

                    for (Object o : q) {
                        System.out.println("************");
                        if (o == null) {                                     // (2)
                            child.onNext(null);
                            e++;
                        } else if (o == COMPLETED_SENTINEL) {                // (3)
                            child.onCompleted();
                            return;
                        } else if (o.getClass() == ErrorSentinel.class) {    // (4)
                            child.onError(((ErrorSentinel)o).error);
                            return;
                        } else if (o.getClass() == ProducerSentinel.class) { // (5)
                            Producer p = (Producer)o;
                            currentProducer = p;
                            long r = requested;
                            if (p != null && r != 0) {
                                p.request(r);
                            }
                        } else if (o.getClass() == RequestSentinel.class) {  // (6)
                            long n = ((RequestSentinel)o).n;
                            long u = requested + n;
                            if (u < 0) {
                                u = Long.MAX_VALUE;
                            }
                            requested = u;
                            Producer p = currentProducer;
                            if (p != null) {
                                p.request(n);
                            }
                        } else {                                             // (7)
                            child.onNext((T)o);
                            e++;
                        }
                    }
                    long r = requested;
                    if (r != Long.MAX_VALUE) {                               // (8)
                        long v = requested - e;
                        if (v < 0) {
                            throw new IllegalStateException();
                        }
                        requested = v;
                    }
                }
            }
        }

        class SwitchTimer<T> implements Observable.OnSubscribe<T> {
            final List<Observable<? extends T>> sources;
            final long time;
            final TimeUnit unit;
            final Scheduler scheduler;

            public SwitchTimer(Iterable<? extends Observable<? extends T>> sources, long time, TimeUnit unit, Scheduler scheduler) {
                this.scheduler = scheduler;
                this.sources = new ArrayList<>();
                this.time = time;
                this.unit = unit;
                sources.forEach(this.sources::add);
            }
            @Override
            public void call(Subscriber<? super T> child) {
                ProducerObserverArbiter<T> poa = new ProducerObserverArbiter<>(child);             // (1)

                Scheduler.Worker w = scheduler.createWorker();        // (2)
                child.add(w);

                child.setProducer(poa);

                // 为 Observable 序列保存 Subscriber 引用，并把它和 child 级联起来，以便在 child 被取消订阅时可以一同被取消订阅
                SerialSubscription ssub = new SerialSubscription();   // (3)
                child.add(ssub);

                int[] index = new int[1];

                w.schedulePeriodically(() -> {
                    int idx = index[0]++;
                    if (idx >= sources.size()) {                      // (4)
                        poa.onCompleted();
                        return;
                    }
                    Subscriber<T> s = new Subscriber<T>() {           // (5)
                        @Override
                        public void onNext(T t) {
                            poa.onNext(t);
                        }
                        @Override
                        public void onError(Throwable e) {
                            poa.onError(e);
                        }
                        @Override
                        public void onCompleted() {
                            if (idx + 1 == sources.size()) {          // (6)
                                poa.onCompleted();
                            }
                        }
                        @Override
                        public void setProducer(Producer producer) {
                            System.out.println("faefafeafef");
                            poa.set(producer);
                        }
                    };

                    // 新的 Observable 被订阅时，老的订阅会被取消
                    ssub.set(s);                                      // (7)
                    System.out.println("----------------");
                    sources.get(idx).unsafeSubscribe(s);

                }, time, time, unit);
            }
        }

        List<Observable<Long>> timers = Arrays.asList(
                Observable.timer(100, 100, TimeUnit.MILLISECONDS),
                Observable.timer(100, 100, TimeUnit.MILLISECONDS)
                        .map(v -> v + 20),
                Observable.timer(100, 100, TimeUnit.MILLISECONDS)
                        .map(v -> v + 40)
        );

        Observable<Long> source = Observable.create(new SwitchTimer<>(timers, 500, TimeUnit.MILLISECONDS, Schedulers.computation()));


        source.subscribe(new PrintSubscriber("222"));

        TimeUnit.SECONDS.sleep(2);
        source.subscribe(new PrintSubscriber("333"));

        TimeUnit.SECONDS.sleep(2);
//        System.in.read();
    }

}
