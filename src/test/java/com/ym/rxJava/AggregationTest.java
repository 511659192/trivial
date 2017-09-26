package com.ym.rxJava;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.operators.OperatorMerge;
import rx.observables.GroupedObservable;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/3.
 */
public class AggregationTest {

    @Test
    public void testCount() throws Exception {
        Observable<Integer> values = Observable.range(0, 3);
        values.subscribe(new PrintSubscriber("Values"));
        // 如果发射数据的个数超过了 int 最大值，则可以使用 countLong 函数。
        values.count()
            .subscribe(new PrintSubscriber("Count"));
    }

    @Test
    public void testFirst() throws Exception {
        Observable<Long> values = Observable.just(1L, 2L, 3L);
        values
            .first(v -> v > 1)
//            .firstOrDefault(12L, v -> v > 5)
//            .last()
//            .firstOrDefault(12L, v -> v < 5)
            .subscribe(new PrintSubscriber("First"));
    }

    @Test
    public void testSingle() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);
        values.take(10) // 获取前 10 个数据 的 Observable
            .single(v -> v == 5L) // 有且仅有一个 数据为 5L
            .subscribe(new PrintSubscriber("Single1"));
        values.single(v -> v == 5L) // 由于源 Observable 为无限的，所以这个不会打印任何东西
            .subscribe(new PrintSubscriber("Single2"));
        System.in.read();
    }

    @Test
    public void testReduce() throws Exception {
        Observable<Integer> values = Observable.range(0,5);
        values.reduce((i1, i2) -> i1+i2)
            .subscribe(new PrintSubscriber("Sum"));
        values.reduce((i1, i2) -> (i1 > i2) ? i2 : i1)
            .subscribe(new PrintSubscriber("Min"));

        Observable<String> values2 = Observable.just("Rx", "is", "easy");
        values2.reduce(0, (acc,next) -> acc + 1)
            .subscribe(new PrintSubscriber("Count"));
    }

    @Test
    public void testScan() throws Exception {
        Observable<Integer> values = Observable.range(0,5);
        values.scan((i1,i2) -> i1 + i2) // scan 和 reduce 很像，不一样的地方在于 scan会发射所有中间的结算结果。
            .subscribe(new PrintSubscriber("Sum"));
    }

    @Test
    public void testReduceToArr() throws Exception {
        Observable<Integer> values = Observable.range(10,5);
        values
            .reduce(new ArrayList<Integer>(),
                (acc, value) -> {
                    acc.add(value);
                    return acc;
                })
            .subscribe(new Action1<ArrayList<Integer>>() {
                @Override
                public void call(ArrayList<Integer> integers) {
                    System.out.println(integers);
                }
            });
        System.out.println("-----------------------");
        values
            .reduce(new ArrayList<Integer>(),
                (acc, value) -> {
                    ArrayList<Integer> newAcc = (ArrayList<Integer>) acc.clone();
                    newAcc.add(value);
                    return newAcc;
                })
            .subscribe(v -> System.out.println(v));
    }

    @Test
    public void testCollect() throws Exception {
        Observable<Integer> values = Observable.range(10,5);
        values
            .collect(() -> new ArrayList<Integer>(),
                    (acc, value) -> acc.add(value))
                .flatMap(new Func1<ArrayList<Integer>, Observable<?>>() {
                    @Override
                    public Observable<?> call(ArrayList<Integer> integers) {
                        return Observable.from(integers);
                    }
                })
            .subscribe(v -> System.out.println(v));
    }

    @Test
    public void testToList() throws Exception {
        Observable<Integer> values = Observable.range(10,5);

        values
            .toList()
            .subscribe(v -> System.out.println(v));
    }

    @Test
    public void testToSortedList() throws Exception {
        Observable<Integer> values = Observable.range(10,5);

        values
            .toSortedList((i1,i2) -> i2 - i1)
            .subscribe(v -> System.out.println(v));
    }

    @Test
    public void testToMap() throws Exception {
        Observable<Person> values = Observable.just(
                new Person("Will", 25),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values
            .toMap(person -> person.name)
            .subscribe(new PrintSubscriber("toMap"));


        System.out.println("-----------------------------");
        values
            .toMap(
                    person -> person.name,
                    person -> person.age)
            .subscribe(new PrintSubscriber("toMap"));

        System.out.println("-----------------------------");
        values
            .toMap(
                    person -> person.name,
                    person -> person.age,
                    () -> new HashMap())
            .subscribe(new PrintSubscriber("toMap"));
    }

    @Test
    public void testToMultimap() throws Exception {

        Observable<Person> values = Observable.just(
                new Person("Will", 35),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values
            .toMultimap(
                    person -> person.age,
                    person -> person.name)
            .subscribe(new PrintSubscriber("toMap"));

        System.out.println("-------------------------");
        values
            .toMultimap(
                    person -> person.age,
                    person -> person.name,
                    () -> new HashMap(),
                    (key) -> new ArrayList()) // 没有使用这个 key 参数
            .subscribe(new PrintSubscriber("toMap"));

    }

    @Test
    public void testGroupBy() throws Exception {
        Observable<String> values = Observable.just(
            "first",
            "forth",
//            "fifth",
//            "second",
            "third",
            "sixth"
        );

        values.groupBy(word -> word.charAt(0))
//            .flatMap(group -> group.map(v -> group.getKey() + ": " + v))
//            .flatMap(new Func1<GroupedObservable<Character, String>, Observable<?>>() {
//                @Override
//                public Observable<?> call(GroupedObservable<Character, String> characterStringGroupedObservable) {
//                    return characterStringGroupedObservable;
//                }
//            })
//                .map(group -> group)
//                .lift(OperatorMerge.instance(false))
//            .doOnNext(new Action1<GroupedObservable<Character, String>>() {
//                @Override
//                public void call(GroupedObservable<Character, String> characterStringGroupedObservable) {
//                    System.out.println("11");
//                }
//            })
            .subscribe(v -> System.out.println(v));
    }

    @Test
    public void testNest() throws Exception {
        Observable.range(0, 3)
            .nest()
            .subscribe(ob -> ob.subscribe(System.out::println));
    }

    class Person {
        public final String name;
        public final Integer age;
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
