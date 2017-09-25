package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/4.
 */
public class TransformationTest {

    @Test
    public void testMap() throws Exception {
        Observable<Integer> values = Observable.range(0,4);

        values
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return String.valueOf(integer);
                    }
                })
                .subscribe(new PrintSubscriber("Map"));
    }

    @Test
    public void testMap2() throws Exception {
        Observable<Integer> values =
                Observable.just("0", "1", "2", "3")
                        .map(Integer::parseInt);

        values.subscribe(new PrintSubscriber("Map"));
    }

    @Test
    public void testCast() throws Exception {
        Observable<Object> values = Observable.just(0, 1, 2, 3);

        values
                .cast(Integer.class)
                .subscribe(new PrintSubscriber("Map"));

        System.out.println("-------------------");
        Observable<Object> values2 = Observable.just(0, 1, 2, "3");
        values2
                .cast(Integer.class) // 如果遇到类型不一样的对象的话，就会抛出一个 error
                .subscribe(new PrintSubscriber("Map"));
    }

    @Test
    public void testOfType() throws Exception {
        Observable<Object> values = Observable.just(0, 1, "2", 3);

        values
                // 处理类型不一样的对象 该函数用来判断数据是否为 该类型，如果不是则跳过这个数据。
                .ofType(Integer.class)
                .subscribe(new PrintSubscriber("Map"));
    }

    @Test
    public void testTimestamp() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(3)
                .timestamp()
                .subscribe(new PrintSubscriber("Timestamp"));
        System.in.read();
    }

    @Test
    public void testTimeInterval() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(3)
                // 前一个数据和当前数据发射直接的时间间隔
                .timeInterval()
                .subscribe(new PrintSubscriber("TimeInterval"));
        System.in.read();
    }

    @Test
    public void testMaterialize () throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(3)
                // materialize 把数据转换为元数据发射出去
                .materialize()
                .subscribe(new PrintSubscriber("Materialize"));
        System.in.read();
    }

    @Test
    public void testFlatMap() throws Exception {
        System.out.println(Long.MIN_VALUE);
        System.out.println(Long.MAX_VALUE);

        Observable<Integer> values = Observable.just(2, 20);

        values
                .flatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<?> call(Integer integer) {
//                        return Observable.just(integer);
                        return Observable.range(integer, 2);
                    }
                })
                .subscribe(new PrintSubscriber("testFlatMap"));
//                .map(new Func1<Integer, Observable<?>>() {
//                    @Override
//                    public Observable<?> call(Integer integer) {
//                        return Observable.range(integer, 2);
//                    }
//                })
//                .subscribe(System.out::println);
    }

    @Test
    public void testFlatMap2() throws Exception {
        Observable<Integer> values = Observable.range(0,30);

        values
//                .flatMap(i -> {
//                    if (0 < i && i <= 26)
//                        return Observable.just(i);
//                    else {
//                        // 源 Observable 中发射的数据不符合你的要求，则你可以返回一个 空的 Observable。这就相当于过滤数据的作用
//                        return Observable.empty();
//                    }
//                })
                .flatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<?> call(Integer integer) {
                        return Observable.empty();
                    }
                })
                .subscribe(new PrintSubscriber("flatMap"));
    }

    @Test
    public void testFlatMap3() throws Exception {
        Observable.just(100, 150)
                .flatMap(i ->
                        Observable.interval(i, TimeUnit.MILLISECONDS)
                                .map(v -> i)
                )
                .take(10)
                .timeInterval()
                .subscribe(new PrintSubscriber("flatMap"));
        System.in.read();
    }

    @Test
    public void testConcatMap() throws Exception {
        Observable.just(100, 150)
                .concatMap(i ->
                        Observable.interval(i, TimeUnit.MILLISECONDS)
                                .map(v -> i)
                                .take(3))
                .subscribe(
                        System.out::println,
                        System.out::println,
                        () -> System.out.println("Completed"));
        System.in.read();
    }

    @Test
    public void testFlatMapIterable() throws Exception {
        Observable.just(1, 6)
                .flatMapIterable(i -> range(i, 2)) // flatMapIterable 把生成的 3 个 iterable 合并为一个 Observable 发射。
                .subscribe(System.out::println);


        System.out.println("--------------------------");
        Observable.just(1, 3)
                .flatMapIterable(
                        i -> range(i, 2),
                        (ori, rv) -> ori * (Integer) rv)
                .subscribe(System.out::println);

    }

    public static Iterable<Integer> range(int start, int count) {
        List<Integer> list = new ArrayList<>();
        for (int i=start ; i<start+count ; i++) {
            list.add(i);
        }
        return list;
    }
}
