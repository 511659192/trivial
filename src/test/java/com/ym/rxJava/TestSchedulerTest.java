package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by yangm on 2017/9/8.
 */
public class TestSchedulerTest {

    @Test
    public void testAdvanceTimeTo() throws Exception {
        TestScheduler s = Schedulers.test();

        s.createWorker().schedule(
                () -> System.out.println("Immediate"));
        s.createWorker().schedule(
                () -> System.out.println("20s"),
                20, TimeUnit.SECONDS);
        s.createWorker().schedule(
                () -> System.out.println("40s"),
                40, TimeUnit.SECONDS);

        System.out.println("Advancing to 1ms");
        s.advanceTimeTo(1, TimeUnit.MILLISECONDS);
        System.out.println("Virtual time: " + s.now());

        System.out.println("Advancing to 10s");
        s.advanceTimeTo(10, TimeUnit.SECONDS);
        System.out.println("Virtual time: " + s.now());

        System.out.println("Advancing to 40s");
        s.advanceTimeTo(40, TimeUnit.SECONDS);
        System.out.println("Virtual time: " + s.now());
    }

    @Test
    public void testAdvanceTimeBy() throws Exception {
        TestScheduler s = Schedulers.test();

        s.createWorker().schedule(
                () -> System.out.println("Immediate"));
        s.createWorker().schedule(
                () -> System.out.println("20s"),
                20, TimeUnit.SECONDS);
        s.createWorker().schedule(
                () -> System.out.println("40s"),
                40, TimeUnit.SECONDS);

        System.out.println("Advancing by 1ms");
        s.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        System.out.println("Virtual time: " + s.now());

        System.out.println("Advancing by 10s");
        s.advanceTimeBy(10, TimeUnit.SECONDS);
        System.out.println("Virtual time: " + s.now());

        System.out.println("Advancing by 40s");
        s.advanceTimeBy(40, TimeUnit.SECONDS);
        System.out.println("Virtual time: " + s.now());
    }

    @Test
    public void testTriggerActions() throws Exception {
        TestScheduler s = Schedulers.test();

        s.createWorker().schedule(
                () -> System.out.println("Immediate"));
        s.createWorker().schedule(
                () -> System.out.println("20s"),
                20, TimeUnit.SECONDS);

        s.triggerActions();
        System.out.println("Virtual time: " + s.now());
        s.advanceTimeBy(40, TimeUnit.SECONDS);
        s.triggerActions();
        System.out.println("Virtual time: " + s.now());
    }

    @Test
    public void testWorkeSeq() throws Exception {
        TestScheduler s = Schedulers.test();

        s.createWorker().schedule(
                () -> System.out.println("First"),
                20, TimeUnit.SECONDS);
        s.createWorker().schedule(
                () -> System.out.println("Second"),
                20, TimeUnit.SECONDS);
        s.createWorker().schedule(
                () -> System.out.println("Third"),
                20, TimeUnit.SECONDS);

        s.advanceTimeTo(20, TimeUnit.SECONDS);
    }

    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();
        List<Long> expected = Arrays.asList(0L, 1L, 2L, 3L, 4L);
        List<Long> result = new ArrayList<>();
        Observable
                .interval(1, TimeUnit.SECONDS, scheduler)
                .take(5)
                .subscribe(i -> result.add(i));
        assertTrue(result.isEmpty());
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS);
        assertTrue(result.equals(expected));
    }

    @Test
    public void testAssertReceivedOnNext() {
        TestScheduler scheduler = new TestScheduler();
        TestSubscriber<Long> subscriber = new TestSubscriber<>();
        List<Long> expected = Arrays.asList(0L, 1L, 2L, 3L, 4L);
        Observable
                .interval(1, TimeUnit.SECONDS, scheduler)
                .take(5)
                .subscribe(subscriber);
        assertTrue(subscriber.getOnNextEvents().isEmpty());
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS);
        subscriber.assertReceivedOnNext(expected);
    }
}
