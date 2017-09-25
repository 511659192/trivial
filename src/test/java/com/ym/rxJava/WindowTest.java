package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/8.
 */
public class WindowTest {

    @Test
    public void testWindowByCount() throws Exception {
        Observable
                .concat(
                        Observable.range(0, 5)
                                .window(3,1))
                .subscribe(System.out::println);
        System.out.println("----------------------");


        Observable.range(0, 5)
                .window(3,1)
                .flatMap(o -> o.toList())
                .subscribe(System.out::println);
    }

    @Test
    public void testWindowByTime() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(5)
                .window(250, 100, TimeUnit.MILLISECONDS)
                .flatMap(o -> o.toList())
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(3);
        System.out.println("----------------------");

        Observable.interval(100, TimeUnit.MILLISECONDS)
                .timeInterval()
                .take(5)
                .window(250, 100, TimeUnit.MILLISECONDS)
                .flatMap(o -> o.toList())
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testWindowWithSignal() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(5)
                .window(
                        Observable.interval(100, TimeUnit.MILLISECONDS),
                        o -> Observable.timer(250, TimeUnit.MILLISECONDS))
                .flatMap(o -> o.toList())
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testJoin() throws Exception {
        Observable<String> left =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .map(i -> "L" + i);
        Observable<String> right =
                Observable.interval(200, TimeUnit.MILLISECONDS)
                        .map(i -> "R" + i);

        left
                .join(
                        right,
                        i -> Observable.never(),
                        i -> Observable.timer(0, TimeUnit.MILLISECONDS),
                        (l,r) -> l + " - " + r
                )
                .take(10)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testJoin2() throws Exception {
        Observable<String> left =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .map(i -> "L" + i);
        Observable<String> right =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .map(i -> "R" + i);

        left
                .join(
                        right,
                        i -> Observable.timer(10, TimeUnit.MILLISECONDS),
                        i -> Observable.timer(0, TimeUnit.MILLISECONDS),
                        (l,r) -> l + " - " + r
                )
                .take(10)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testGroupJoin() throws Exception {
        Observable<String> left =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .map(i -> "L" + i)
                        .take(6);
        Observable<String> right =
                Observable.interval(200, TimeUnit.MILLISECONDS)
                        .map(i -> "R" + i)
                        .take(3);

        left
                .groupJoin(
                        right,
                        i -> Observable.never(),
                        i -> Observable.timer(0, TimeUnit.MILLISECONDS),
                        (l, rs) -> {
                            System.out.println("l " + l + " rs " + rs);
                            rs.subscribe(new PrintSubscriber(l));
                            return rs.toList().subscribe(list -> System.out.println(l + ": " + list));
                        }
                )
                .subscribe();
        System.in.read();
    }



    @Test
    public void testGroupJoin2() throws Exception {
        Long startTime = System.currentTimeMillis();
        Observable<String> left =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .map(i -> "L" + i)
                        .take(6);
        Observable<String> right =
                Observable.interval(200, TimeUnit.MILLISECONDS)
                        .map(i -> "R" + i)
                        .take(3);

        left
                .groupJoin(
                        right,
                        i -> Observable.never(),
                        i -> Observable.timer(0, TimeUnit.MILLISECONDS),
                        (l, rs) -> {
                            rs.subscribe(new PrintSubscriber(System.currentTimeMillis() -startTime + " " + l));
                            return rs.map(r -> l + " - " + r);
                        }
                )
                .flatMap(i -> i)
                .subscribe(System.out::println);
        System.in.read();
    }
}
