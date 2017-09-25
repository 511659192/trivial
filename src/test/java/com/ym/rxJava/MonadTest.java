package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

import javax.sound.midi.Soundbank;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/5.
 */
public class MonadTest {

    @Test
    public void testBlocking() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values
                .take(5)
//                .toBlocking()
                .forEach(v -> System.out.println(v));
        System.out.println("Subscribed");
        System.in.read();
    }

    @Test
    public void testBlocking2() throws Exception {
        Observable.just(1,2)
//                .map(i -> i + " ***")
                .observeOn(Schedulers.newThread())
                .toBlocking()
//                .forEach(v -> System.out.println(v));
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);
                    }
                });
        System.out.println("Subscribed");
    }

    @Test
    public void testThread() throws Exception {
        final BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("1111");
                queue.offer(1111);
                queue.offer(22);
            }
        }).start();
//        TimeUnit.SECONDS.sleep(1);
        Integer aa = queue.take();

        System.out.println("1212");
    }

    @Test
    public void testBlockingFirst() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        long value = values
                .take(5)
                .toBlocking()
                .first(i -> i>2);
        System.out.println(value);
        System.in.read();
    }

    @Test
    public void testIterable() throws Exception {
        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

        Iterable<Long> iterable = values.take(5).toBlocking().toIterable();
        for (long l : iterable) {
            System.out.println(l);
        }
        System.in.read();
    }

    @Test
    public void testNext() throws Exception {
        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

        values.take(5)
                .subscribe(v -> System.out.println("Emitted: " + v));

        // 打印语句（消费者）处理的速度比数据发射的速度慢。所以消费者会错过一些数据
        Iterable<Long> iterable = values.toBlocking().next();
        for (long l : iterable) {
            System.out.println(l);
            Thread.sleep(750);
        }
        System.in.read();
    }

    @Test
    public void testLatest() throws Exception {
        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

        values.take(5)
                .subscribe(v -> System.out.println("Emitted: " + v));

        Iterable<Long> iterable = values.take(5).toBlocking().latest();
        for (long l : iterable) {
            System.out.println(l);
            Thread.sleep(750);
        }
        System.in.read();
    }

    @Test
    public void testMostRecent() throws Exception {
        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

        values.take(5)
                .subscribe(v -> System.out.println("Emitted: " + v));

        Iterable<Long> iterable = values.take(5).toBlocking().mostRecent(-1L);
        for (long l : iterable) {
            System.out.println(l);
            Thread.sleep(400);
        }
        System.in.read();
    }

    @Test
    public void testFuture() throws Exception {
        Observable<Long> values = Observable.timer(500, TimeUnit.MILLISECONDS);

        values.subscribe(v -> System.out.println("Emitted: " + v));

        Future<Long> future = values.toBlocking().toFuture();
        System.out.println(future.get());
    }

    @Test
    public void testDeadlocks() throws Exception {
        ReplaySubject<Integer> subject = ReplaySubject.create();

        subject
//                .toBlocking()
                // forEach 只有当 Observable 结束发射的时候才返回。而后面的 onNext 和 onCompleted 需要 forEach 返回后才能执行，这样就导致了死锁。所以 forEach 会一直等待下去
                .forEach(v -> System.out.println(v));
        subject.onNext(1);
        subject.onNext(2);
        subject.onCompleted();
    }
}
